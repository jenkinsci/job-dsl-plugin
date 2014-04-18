package javaposse.jobdsl.dsl.views.jobfilters

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType

class JobStatusFilterContext implements Context {

    String includeExcludeType
    boolean unstable = false
    boolean failed = false
    boolean aborted = false
    boolean disabled = false
    boolean stable = false

    public JobStatusFilterContext(IncludeExcludeType type) {
        this.includeExcludeType = type.value
    }

    void unstable(boolean unstable = true) {
        this.unstable = unstable
    }

    void failed(boolean failed = true) {
        this.failed = failed
    }

    void aborted(boolean aborted = true) {
        this.aborted = aborted
    }

    void disabled(boolean disabled = true) {
        this.disabled = disabled
    }

    void stable(boolean stable = true) {
        this.stable = stable
    }
}
