package javaposse.jobdsl.plugin.structs

import hudson.model.Descriptor
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.plugin.Messages
import jenkins.model.Jenkins
import org.jenkinsci.plugins.structs.describable.DescribableModel

import static java.lang.String.format
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static DescribableHelper.findDescribableModels
import static DescribableHelper.isOptionalClosureArgument

/**
 * A dynamic {@link Context} that can be used to extend the DSL by a list of {@link hudson.model.Describable}s.
 *
 * @since 1.46
 */
class DescribableListContext implements Context {
    private final Collection<DescribableModel> describableModels
    private final JobManagement jobManagement
    final List values = []

    /**
     * @since 1.58
     */
    DescribableListContext(String type, JobManagement jobManagement) {
        this(
                Jenkins.instance.getExtensionList(type).collect { Descriptor d -> new DescribableModel(d.clazz) },
                jobManagement
        )
    }

    DescribableListContext(Collection<DescribableModel> types, JobManagement jobManagement) {
        this.describableModels = types
        this.jobManagement = jobManagement
    }

    Object methodMissing(String name, args) {
        Object[] argsArray = (Object[]) args
        if (isOptionalClosureArgument(argsArray)) {
            Collection<DescribableModel> candidates = findDescribableModels(describableModels, name)
            if (candidates.size() > 1) {
                throw new DslException(format(
                        Messages.CallExtension_MultipleCandidates(),
                        name,
                        Arrays.toString(argsArray),
                        Arrays.toString(candidates*.type*.name)
                ))
            } else if (candidates.size() == 1) {
                DescribableModel candidate = candidates.first()
                if (candidate.deprecated) {
                    jobManagement.logDeprecationWarning(name)
                }
                DescribableContext delegate = new DescribableContext(candidate, jobManagement)
                if (args.size() == 1) {
                    executeInContext((Closure) argsArray[0], delegate)
                }
                values << delegate.createInstance()
                return null
            }
        }
        throw new MissingMethodException(name, DescribableListContext, argsArray)
    }
}
