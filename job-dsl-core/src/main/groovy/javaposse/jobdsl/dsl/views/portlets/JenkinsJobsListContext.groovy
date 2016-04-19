package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class JenkinsJobsListContext implements Context {
    String displayName = 'Jenkins Jobs List'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Jenkins Jobs List'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }
}
