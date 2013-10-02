package javaposse.jobdsl.dsl.helpers.publisher

class WarningsContext extends StaticAnalysisContext {

    String includePattern = ''
    String excludePattern = ''

    def resolveRelativePaths(boolean resolveRelativePaths = true) {
        this.doNotResolveRelativePaths = ! resolveRelativePaths
    }

    def includePattern(String includePattern) {
        this.includePattern = includePattern
    }

    def excludePattern(String excludePattern) {
        this.excludePattern = excludePattern
    }
}
