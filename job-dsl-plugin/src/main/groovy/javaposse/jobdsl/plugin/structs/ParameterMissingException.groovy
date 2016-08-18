package javaposse.jobdsl.plugin.structs

import org.jenkinsci.plugins.structs.describable.DescribableModel

import static org.codehaus.groovy.runtime.InvokerHelper.format
import static org.codehaus.groovy.runtime.InvokerHelper.toTypeString

/**
 * This exception is thrown when a dynamic method dispatch within a {@link DescribableContext} fails because the
 * corresponding {@link hudson.model.Describable} has no parameter matching the invoked method's name.
 *
 * @since 1.46
 */
class ParameterMissingException extends MissingMethodException {
    private final DescribableModel model

    ParameterMissingException(String method, Class type, Object[] arguments, DescribableModel model) {
        super(method, type, arguments)
        this.model = model
    }

    @Override
    String getMessage() {
        new StringBuilder()
                .append('No signature of method: ')
                .append(method)
                .append('() is applicable for argument types: (')
                .append(toTypeString(arguments))
                .append(') values: ')
                .append(format(arguments, false, 40))
                .append('\nPossible solutions: ')
                .append(model.parameters.collect { "$it.name()" }.join(', '))
                .toString()
    }
}
