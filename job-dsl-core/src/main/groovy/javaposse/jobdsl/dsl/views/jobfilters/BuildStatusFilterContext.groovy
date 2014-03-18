package javaposse.jobdsl.dsl.views.jobfilters

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType

class BuildStatusFilterContext implements Context {

    String includeExcludeType = IncludeExcludeType.INCLUDE_MATCHED.value
    boolean neverBuilt = false
    boolean building = false
    boolean inBuildQueue = false

    void includeExcludeType(IncludeExcludeType type) {
        this.includeExcludeType = type.value
    }

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
