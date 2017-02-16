package javaposse.jobdsl.plugin.structs

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import org.apache.commons.lang.ClassUtils
import org.jenkinsci.plugins.structs.describable.ArrayType
import org.jenkinsci.plugins.structs.describable.AtomicType
import org.jenkinsci.plugins.structs.describable.DescribableModel
import org.jenkinsci.plugins.structs.describable.DescribableParameter
import org.jenkinsci.plugins.structs.describable.EnumType
import org.jenkinsci.plugins.structs.describable.HeterogeneousObjectType
import org.jenkinsci.plugins.structs.describable.HomogeneousObjectType
import org.jenkinsci.plugins.structs.describable.ParameterType

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

/**
 * A dynamic {@link Context} that can be used to extend the DSL for any {@link hudson.model.Describable}.
 *
 * @since 1.46
 */
class DescribableContext implements Context {
    private final DescribableModel describableModel
    private final JobManagement jobManagement
    private final Map<String, ?> values = [:]

    DescribableContext(DescribableModel describableModel, JobManagement jobManagement) {
        this.describableModel = describableModel
        this.jobManagement = jobManagement
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
                if (parameter.deprecated) {
                    jobManagement.logDeprecationWarning(name)
                }
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
                    return value.every { isAssignable(it, (AtomicType) arrayType.elementType) }
                } else if (arrayType.elementType instanceof EnumType) {
                    EnumType enumType = (EnumType) arrayType.elementType
                    value.each { checkValidEnumValue(enumType, it) }
                    return true
                }
            }
        } else if (parameterType instanceof EnumType) {
            checkValidEnumValue((EnumType) parameterType, value)
            return true
        } else if (parameterType instanceof AtomicType) {
            return (value != null && isAssignable(value, parameterType)) ||
                    (value == null && !((AtomicType) parameterType).type.primitive)
        } else if (parameterType instanceof HomogeneousObjectType) {
            return value instanceof Closure || value == null
        } else if (parameterType instanceof HeterogeneousObjectType) {
            return value instanceof Closure || value == null
        }
        false
    }

    private Object getValue(ParameterType parameterType, Object value) {
        if (value instanceof Closure) {
            if (parameterType instanceof ArrayType) {
                DescribableListContext delegate = new DescribableListContext(
                        getArrayElementTypes((ArrayType) parameterType),
                        jobManagement
                )
                executeInContext(value, delegate)
                return delegate.values
            } else if (parameterType instanceof HomogeneousObjectType) {
                DescribableContext delegate = new DescribableContext(
                        ((HomogeneousObjectType) parameterType).schemaType,
                        jobManagement
                )
                executeInContext((Closure) value, delegate)
                return delegate.createInstance()
            } else if (parameterType instanceof HeterogeneousObjectType) {
                DescribableListContext delegate = new DescribableListContext(
                        ((HeterogeneousObjectType) parameterType).types.values(),
                        jobManagement
                )
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

    private static boolean isAssignable(Object value, AtomicType parameterType) {
        Class normalizedType = value instanceof GString ? String : value.class
        ClassUtils.isAssignable(normalizedType, parameterType.type, true)
    }

    private static void checkValidEnumValue(EnumType enumType, Object value) {
        if (!enumType.type.isInstance(value) && !enumType.values.contains(value)) {
            throw new DslScriptException(
                    "invalid enum value '${value}', must be one of '${enumType.values.join("', '")}'"
            )
        }
    }
}
