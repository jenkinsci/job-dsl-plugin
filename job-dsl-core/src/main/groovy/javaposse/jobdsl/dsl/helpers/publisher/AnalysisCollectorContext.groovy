package javaposse.jobdsl.dsl.helpers.publisher

class AnalysisCollectorContext extends StaticAnalysisContext {
    boolean includeCheckstyle = false
    boolean includeDry = false
    boolean includeFindbugs = false
    boolean includePmd = false
    boolean includeTasks = false
    boolean includeWarnings = false

    /**
     * If set, aggregates Checkstyle warnings. Defaults to {@code false}.
     */
    void checkstyle(boolean includeCheckstyle = true) {
        this.includeCheckstyle = includeCheckstyle
    }

    /**
     * If set, aggregates duplicate code warnings. Defaults to {@code false}.
     */
    void dry(boolean includeDry = true) {
        this.includeDry = includeDry
    }

    /**
     * If set, aggregates FindBugs warnings. Defaults to {@code false}.
     */
    void findbugs(boolean includeFindbugs = true) {
        this.includeFindbugs = includeFindbugs
    }

    /**
     * If set, aggregates PMD warnings. Defaults to {@code false}.
     */
    void pmd(boolean includePmd = true) {
        this.includePmd = includePmd
    }

    /**
     * If set, aggregates open tasks. Defaults to {@code false}.
     */
    void tasks(boolean includeTasks = true) {
        this.includeTasks = includeTasks
    }

    /**
     * If set, aggregates compiler warnings. Defaults to {@code false}.
     */
    void warnings(boolean includeWarnings = true) {
        this.includeWarnings = includeWarnings
    }
}
