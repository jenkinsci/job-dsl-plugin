package javaposse.jobdsl.dsl.helpers.publisher

class AnalysisCollectorContext extends StaticAnalysisContext {
    boolean includeCheckstyle = false
    boolean includeDry = false
    boolean includeFindbugs = false
    boolean includePmd = false
    boolean includeTasks = false
    boolean includeWarnings = false

    def checkstyle(boolean includeCheckstyle = true) {
        this.includeCheckstyle = includeCheckstyle
    }

    def dry(boolean includeDry = true) {
        this.includeDry = includeDry
    }

    def findbugs(boolean includeFindbugs = true) {
        this.includeFindbugs = includeFindbugs
    }

    def pmd(boolean includePmd = true) {
        this.includePmd = includePmd
    }

    def tasks(boolean includeTasks = true) {
        this.includeTasks = includeTasks
    }

    def warnings(boolean includeWarnings = true) {
        this.includeWarnings = includeWarnings
    }
}
