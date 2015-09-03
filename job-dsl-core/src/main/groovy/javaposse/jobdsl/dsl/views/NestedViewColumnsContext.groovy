package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

class NestedViewColumnsContext implements Context {
    List<Node> columnNodes = []

    /**
     * Adds a column showing the status of the last build.
     */
    void status() {
        columnNodes << new Node(null, 'hudson.views.StatusColumn')
    }

    /**
     * Adds a weather report showing the aggregated status of recent builds.
     */
    void weather() {
        columnNodes << new Node(null, 'hudson.views.WeatherColumn')
    }
}
