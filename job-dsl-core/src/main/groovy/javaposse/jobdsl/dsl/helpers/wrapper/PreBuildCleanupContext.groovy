package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context

class PreBuildCleanupContext implements Context {
    List<Node> patternNodes = []
    boolean deleteDirectories
    String cleanupParameter
    String deleteCommand

    void includePattern(String pattern) {
        addPattern('INCLUDE', pattern)
    }

    void excludePattern(String pattern) {
        addPattern('EXCLUDE', pattern)
    }

    void deleteDirectories(boolean deleteDirectories = true) {
        this.deleteDirectories = deleteDirectories
    }

    void cleanupParameter(String cleanupParameter) {
        this.cleanupParameter = cleanupParameter
    }

    void deleteCommand(String deleteCommand) {
        this.deleteCommand = deleteCommand
    }

    /**
     * <hudson.plugins.ws__cleanup.Pattern>
     *     <pattern>*.class</pattern>
     *     <type>INCLUDE</type>
     * </hudson.plugins.ws__cleanup.Pattern>
     */
    private void addPattern(String type, String pattern) {
        patternNodes << new NodeBuilder().'hudson.plugins.ws__cleanup.Pattern' {
            delegate.pattern(pattern)
            delegate.type(type)
        }
    }
}
