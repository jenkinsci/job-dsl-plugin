package javaposse.jobdsl.dsl.helpers.publisher

class WarningsContext extends StaticAnalysisContext {
    String includePattern = ''
    String excludePattern = ''

    /**
     * Determines if relative paths in warnings should be resolved. Defaults to {@code false}.
     */
    void resolveRelativePaths(boolean resolveRelativePaths = true) {
        this.doNotResolveRelativePaths = !resolveRelativePaths
    }

    /**
     * Sets a comma separated list of regular expressions that specifies the files to include in the report (based on
     * their absolute filename).
     */
    void includePattern(String includePattern) {
        this.includePattern = includePattern
    }

    /**
     * Sets a comma separated list of regular expressions that specifies the files to exclude from the report (based on
     * their absolute filename).
     */
    void excludePattern(String excludePattern) {
        this.excludePattern = excludePattern
    }
}
