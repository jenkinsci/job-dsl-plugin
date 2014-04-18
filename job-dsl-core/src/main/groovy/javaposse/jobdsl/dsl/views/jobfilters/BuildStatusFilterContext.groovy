package javaposse.jobdsl.dsl.views.jobfilters

import javaposse.jobdsl.dsl.helpers.Context

class BuildStatusFilterContext implements Context {

    boolean neverBuilt = false
    boolean building = false
    boolean inBuildQueue = false

    void neverBuilt(boolean neverBuilt = true) {
        this.neverBuilt = neverBuilt
    }

    void building(boolean building = true) {
        this.building = building
    }

    void inBuildQueue(boolean inBuildQueue = true) {
        this.inBuildQueue = inBuildQueue
    }
}
