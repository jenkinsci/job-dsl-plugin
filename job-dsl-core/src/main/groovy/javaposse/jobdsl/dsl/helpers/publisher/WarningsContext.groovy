package javaposse.jobdsl.dsl.helpers.publisher

class WarningsContext extends StaticAnalysisContext {

    String includePattern = ''
    String excludePattern = ''

    void resolveRelativePaths(boolean resolveRelativePaths = true) {
        this.doNotResolveRelativePaths = ! resolveRelativePaths
    }

    void includePattern(String includePattern) {
        this.includePattern = includePattern
    }

    void excludePattern(String excludePattern) {
        this.excludePattern = excludePattern
    }
}
