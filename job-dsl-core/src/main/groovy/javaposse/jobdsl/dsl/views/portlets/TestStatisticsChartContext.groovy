package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class TestStatisticsChartContext implements Context {
    String displayName = 'Test Statistics Chart'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Test Statistics Chart'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }
}
