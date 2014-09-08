package javaposse.jobdsl.dsl.helpers.publisher

class AnalysisCollectorContext extends StaticAnalysisContext {

    boolean includeCheckstyle = false
    boolean includeDry = false
    boolean includeFindbugs = false
    boolean includePmd = false
    boolean includeTasks = false
    boolean includeWarnings = false

    def checkstyle(boolean includeCheckstyle = false) {
        this.includeCheckstyle = includeCheckstyle
    }

    def dry(boolean includeDry = false) {
        this.includeDry = includeDry
    }

    def findbugs(boolean includeFindbugs = false) {
        this.includeFindbugs = includeFindbugs
    }

    def pmd(boolean includePmd = false) {
        this.includePmd = includePmd
    }

    def tasks(boolean includeTasks = false) {
        this.includeTasks = includeTasks
    }

    def warnings(boolean includeWarnings = false) {
        this.includeWarnings = includeWarnings
    }
}
