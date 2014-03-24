package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.helpers.Context

class ColumnsContext implements Context {
    List<Node> columnNodes = []

    void status() {
        columnNodes << new Node(null, "hudson.views.StatusColumn")
    }

    void weather() {
        columnNodes << new Node(null, "hudson.views.WeatherColumn")
    }

    void name() {
        columnNodes << new Node(null, "hudson.views.JobColumn")
    }

    void lastSuccess() {
        columnNodes << new Node(null, "hudson.views.LastSuccessColumn")
    }

    void lastFailure() {
        columnNodes << new Node(null, "hudson.views.LastFailureColumn")
    }

    void lastDuration() {
        columnNodes << new Node(null, "hudson.views.LastDurationColumn")
    }

    void buildButton() {
        columnNodes << new Node(null, "hudson.views.BuildButtonColumn")
    }
}
