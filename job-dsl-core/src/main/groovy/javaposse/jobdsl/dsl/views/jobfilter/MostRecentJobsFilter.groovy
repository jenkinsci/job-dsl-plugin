package javaposse.jobdsl.dsl.views.jobfilter

import javaposse.jobdsl.dsl.Context

class MostRecentJobsFilter implements Context {
    int maxToInclude
    boolean checkStartTime

    /**
     * Defined the number of jobs to include. Defaults to {@code 0}.
     */
    void maxToInclude(int maxToInclude) {
        this.maxToInclude = maxToInclude
    }

    /**
     * Use job start time instead of completion time. Defaults to {@code false}.
     */
    void checkStartTime(boolean checkStartTime = true) {
        this.checkStartTime = checkStartTime
    }
}
