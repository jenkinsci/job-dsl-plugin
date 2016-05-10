package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

@ContextType('hudson.plugins.parameterizedtrigger.AbstractBuildParameterFactory')
class DownstreamTriggerParameterFactoryContext extends AbstractExtensibleContext {
    private static final Set<String> VALID_NO_FIILES_FOUND_ACTIONS = ['SKIP', 'NOPARMS', 'FAIL']

    List<Node> configFactories = []

    DownstreamTriggerParameterFactoryContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        configFactories << node
    }

    /**
     * Looks for files that match the specified pattern in the current build, then for each of them trigger a build of
     * the specified project(s) by passing that file as a file parameter.
     *
     * The {@code noFilesFoundAction} must be one of {@code 'SKIP'}, {@code 'NOPARMS'} or {@code 'FAIL'}.
     */
    void forMatchingFiles(String filePattern, String parameterName, String noFilesFoundAction = 'SKIP') {
        Preconditions.checkArgument(
                VALID_NO_FIILES_FOUND_ACTIONS.contains(noFilesFoundAction),
                "noFilesFoundAction must be one of ${VALID_NO_FIILES_FOUND_ACTIONS.join(', ')}"
        )

        configFactories << new NodeBuilder().'hudson.plugins.parameterizedtrigger.BinaryFileParameterFactory' {
            delegate.parameterName(parameterName)
            delegate.filePattern(filePattern)
            delegate.noFilesFoundAction(noFilesFoundAction)
        }
    }
}
