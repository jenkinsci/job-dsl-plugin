package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

class ColumnsContext implements Context {
    List<Node> columnNodes = []

    void status() {
        columnNodes << new Node(null, 'hudson.views.StatusColumn')
    }

    void weather() {
        columnNodes << new Node(null, 'hudson.views.WeatherColumn')
    }

    void name() {
        columnNodes << new Node(null, 'hudson.views.JobColumn')
    }

    void lastSuccess() {
        columnNodes << new Node(null, 'hudson.views.LastSuccessColumn')
    }

    void lastFailure() {
        columnNodes << new Node(null, 'hudson.views.LastFailureColumn')
    }

    void lastDuration() {
        columnNodes << new Node(null, 'hudson.views.LastDurationColumn')
    }

    void buildButton() {
        columnNodes << new Node(null, 'hudson.views.BuildButtonColumn')
    }

    void lastBuildConsole() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.LastBuildConsoleColumn')
    }

    void customIcon() {
        columnNodes << new Node(null, 'jenkins.plugins.jobicon.CustomIconColumn')
    }
}
