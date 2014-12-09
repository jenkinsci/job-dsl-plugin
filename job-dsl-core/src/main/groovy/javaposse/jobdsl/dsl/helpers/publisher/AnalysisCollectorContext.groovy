package javaposse.jobdsl.dsl.helpers.publisher

class AnalysisCollectorContext extends StaticAnalysisContext {
    boolean includeCheckstyle = false
    boolean includeDry = false
    boolean includeFindbugs = false
    boolean includePmd = false
    boolean includeTasks = false
    boolean includeWarnings = false

    void checkstyle(boolean includeCheckstyle = true) {
        this.includeCheckstyle = includeCheckstyle
    }

    void dry(boolean includeDry = true) {
        this.includeDry = includeDry
    }

    void findbugs(boolean includeFindbugs = true) {
        this.includeFindbugs = includeFindbugs
    }

    void pmd(boolean includePmd = true) {
        this.includePmd = includePmd
    }

    void tasks(boolean includeTasks = true) {
        this.includeTasks = includeTasks
    }

    void warnings(boolean includeWarnings = true) {
        this.includeWarnings = includeWarnings
    }
}
