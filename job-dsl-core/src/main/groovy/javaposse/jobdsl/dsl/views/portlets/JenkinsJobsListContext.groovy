package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class JenkinsJobsListContext implements Context {
    String displayName = 'Jenkins jobs list'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Jenkins jobs list'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }
}
