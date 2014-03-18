package javaposse.jobdsl.dsl.views.jobfilters

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType

class OtherViewFilterContext implements Context {

    String includeExcludeType = IncludeExcludeType.INCLUDE_MATCHED.value
    String otherViewName
    

    void includeExcludeType(IncludeExcludeType type) {
        this.includeExcludeType = type.value
    }

    void otherViewName(String otherViewName) {
        this.otherViewName = otherViewName
    }
}
