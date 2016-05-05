package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class TestTrendChartContext implements Context {
    String displayName = 'Test Trend Chart'
    int graphWidth = 300
    int graphHeight = 220
    int dateRange = 0
    int dateShift = 0
    DisplayStatus displayStatus = DisplayStatus.ALL

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Test Trend Chart'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }

    /**
     * Sets the graph width. Defaults to {@code '300'}.
     */
    void graphWidth(int graphWidth) {
        this.graphWidth = graphWidth
    }

    /**
     * Sets the graph height. Defaults to {@code '220'}
     */
    void graphHeight(int graphHeight) {
        this.graphHeight = graphHeight
    }

    /**
     * Sets the number of days to display. Defaults to {@code '0'}
     */
    void dateRange(int dateRange) {
        this.dateRange = dateRange
    }

    /**
     * Sets the number of minutes the date is shifted. Defaults to {@code '0'}
     */
    void dateShift(int dateShift) {
        this.dateShift = dateShift
    }

    /**
     * Sets the display status for this portlet. Defaults to {@code DisplayStatus.ALL}.
     */
    void displayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus
    }

    static enum DisplayStatus {
        ALL, SUCCESS, FAILED, SKIPPED
    }
}
