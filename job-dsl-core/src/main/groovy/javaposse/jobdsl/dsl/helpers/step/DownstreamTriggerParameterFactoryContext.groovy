package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class DownstreamTriggerParameterFactoryContext implements Context {
    private static final Set<String> VALID_NO_FIILES_FOUND_ACTIONS = ['SKIP', 'NOPARMS', 'FAIL']

    List<Node> configFactories = []

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
