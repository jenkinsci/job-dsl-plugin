package javaposse.jobdsl.plugin.structs

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.DslScriptException
import org.jenkinsci.plugins.structs.describable.ArrayType
import org.jenkinsci.plugins.structs.describable.AtomicType
import org.jenkinsci.plugins.structs.describable.DescribableModel
import org.jenkinsci.plugins.structs.describable.DescribableParameter
import org.jenkinsci.plugins.structs.describable.EnumType
import org.jenkinsci.plugins.structs.describable.HeterogeneousObjectType
import org.jenkinsci.plugins.structs.describable.HomogeneousObjectType
import org.jenkinsci.plugins.structs.describable.ParameterType

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static org.apache.commons.lang.ClassUtils.isAssignable

/**
 * A dynamic {@link Context} that can be used to extend the DSL for any {@link hudson.model.Describable}.
 *
 * @since 1.46
 */
class DescribableContext implements Context {
    private final DescribableModel describableModel
    private final Map<String, ?> values = [:]

    DescribableContext(DescribableModel describableModel) {
        this.describableModel = describableModel
    }

    /**
     * Returns an instance of this context's {@Describable}. The instance is populated with values that have been
     * collected by this context.
     */
    Object createInstance() {
        describableModel.parameters.each { DescribableParameter parameter ->
            if (parameter.type instanceof ArrayType && !values.containsKey(parameter.name)) {
                values[parameter.name] = []
            }
        }
        List<String> missingParameters = describableModel.parameters.findAll { it.required }*.name - values.keySet()
        if (missingParameters) {
            throw new DslScriptException(
                    "the following options are required and must be specified: ${missingParameters.join(', ')}"
            )
        }
        describableModel.instantiate(values)
    }

    Object methodMissing(String name, args) {
        Object[] argsArray = (Object[]) args
        if (argsArray.length == 1) {
            Object value = argsArray[0]
            DescribableParameter parameter = describableModel.getParameter(name)
            if (!parameter) {
                throw new ParameterMissingException(name, DescribableContext, argsArray, describableModel)
            }
            if (isValidValue(parameter.type, value)) {
                values[name] = getValue(parameter.type, value)
                return null
            }
        }
        throw new MissingMethodException(name, DescribableContext, argsArray)
    }

    private static boolean isValidValue(ParameterType parameterType, Object value) {
        if (parameterType instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) parameterType
            if (value instanceof Closure) {
                return arrayType.elementType instanceof HeterogeneousObjectType ||
                        arrayType.elementType instanceof HomogeneousObjectType
            } else if (value instanceof Iterable || (value != null && value.class.array)) {
                if (arrayType.elementType instanceof AtomicType) {
                    return value.every { isAssignable(it.class, ((AtomicType) arrayType.elementType).type, true) }
                } else if (arrayType.elementType instanceof EnumType) {
                    EnumType enumType = (EnumType) arrayType.elementType
                    return value.every { enumType.type.isInstance(it) || enumType.values.contains(it) }
                }
            }
        } else if (parameterType instanceof EnumType) {
            EnumType enumType = (EnumType) parameterType
            return enumType.type.isInstance(value) || enumType.values.contains(value)
        } else if (parameterType instanceof AtomicType) {
            return (value != null && isAssignable(value.class, ((AtomicType) parameterType).type, true)) ||
                    (value == null && !((AtomicType) parameterType).type.primitive)
        } else if (parameterType instanceof HomogeneousObjectType) {
            return value instanceof Closure || value == null
        } else if (parameterType instanceof HeterogeneousObjectType) {
            return value instanceof Closure || value == null
        }
        false
    }

    private static Object getValue(ParameterType parameterType, Object value) {
        if (value instanceof Closure) {
            if (parameterType instanceof ArrayType) {
                ArrayType arrayType = (ArrayType) parameterType
                DescribableListContext delegate = new DescribableListContext(getArrayElementTypes(arrayType))
                executeInContext(value, delegate)
                return delegate.values
            } else if (parameterType instanceof HomogeneousObjectType) {
                DescribableContext delegate = new DescribableContext(((HomogeneousObjectType) parameterType).schemaType)
                executeInContext((Closure) value, delegate)
                return delegate.createInstance()
            } else if (parameterType instanceof HeterogeneousObjectType) {
                HeterogeneousObjectType heterogeneousObjectType = (HeterogeneousObjectType) parameterType
                DescribableListContext delegate = new DescribableListContext(heterogeneousObjectType.types.values())
                executeInContext(value, delegate)
                return delegate.values ? delegate.values[-1] : null
            }
        }
        value
    }

    private static Set<DescribableModel> getArrayElementTypes(ArrayType arrayType) {
        if (arrayType.elementType instanceof HeterogeneousObjectType) {
            return ((HeterogeneousObjectType) arrayType.elementType).types.values()
        } else if (arrayType.elementType instanceof HomogeneousObjectType) {
            return [((HomogeneousObjectType) arrayType.elementType).schemaType]
        }
        throw new DslException("unsupported array element type: $arrayType.elementType")
    }
}
