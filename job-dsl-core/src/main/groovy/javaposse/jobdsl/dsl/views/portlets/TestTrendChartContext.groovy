package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.views.DashboardView.DisplayStatus

class TestTrendChartContext implements Context {

    String displayName = 'Test Trend Chart'
    Integer graphWidth = 300
    Integer graphHeight = 200
    Integer dateRange = 30
    Integer dateShift = 0
    DisplayStatus displayStatus = DisplayStatus.ALL

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Test Trend Chart'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }

    /**
     * Sets the graph width for the this portlet. Defaults to {@code '300'}.
     */
    void graphWidth(Integer graphWidth) {
        this.graphWidth = graphWidth
    }

    /**
     * Sets the graph height for this portlet. Defaults to {@code '200'}
     */
    void graphHeight(Integer graphHeight) {
        this.graphHeight = graphHeight
    }

    /**
     * Sets the date range for thies portlet. Defaults to {@code '0'}
     */
    void dateRange(Integer dateRange) {
        this.dateRange = dateRange
    }

    /**
     * Sets the date shift for this portlet. Defaults to {@code '0'}
     */
    void dateShift(Integer dateShift) {
        this.dateShift = dateShift
    }

    /**
     * Sets the display status for this portlet. Defaults to {@code DisplayStatus.ALL}.
     */
    void displayStatus(DisplayStatus displayStatus) {
        this.displayStatus = displayStatus
    }
}
