package javaposse.jobdsl.dsl.views.jobfilter

class BuildStatusFilter extends AbstractJobFilter {
    boolean neverBuilt
    boolean building
    boolean inBuildQueue

    /**
     * Defaults to {@code false}.
     */
    void neverBuilt(boolean neverBuilt = true) {
        this.neverBuilt = neverBuilt
    }

    /**
     * Defaults to {@code false}.
     */
    void building(boolean building = true) {
        this.building = building
    }

    /**
     * Defaults to {@code false}.
     */
    void inBuildQueue(boolean inBuildQueue = true) {
        this.inBuildQueue = inBuildQueue
    }

}
