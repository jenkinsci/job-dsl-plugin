package javaposse.jobdsl.dsl.helpers.publisher

class WarningsContext extends StaticAnalysisContext {
    String includePattern = ''
    String excludePattern = ''
    String messagesPattern = ''
    String categoriesPattern = ''

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

    /**
     * Sets a comma separated list of regular expressions that specifies the warning messages to exclude form the report
     * (based on the warning messages).
     */
    void messagesPattern(String messagesPattern) {
        this.messagesPattern = messagesPattern
    }

    /**
     * Sets a comma separated list of regular expressions that specifies the warning categories to exclude form the
     * report(based on the warning categories).
     */
    void categoriesPattern(String categoriesPattern) {
        this.categoriesPattern = categoriesPattern
    }
}
