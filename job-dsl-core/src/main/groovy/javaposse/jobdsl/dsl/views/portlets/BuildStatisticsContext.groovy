package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class BuildStatisticsContext implements Context {
    String displayName = 'Build statistics'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Build statistics'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }
}
