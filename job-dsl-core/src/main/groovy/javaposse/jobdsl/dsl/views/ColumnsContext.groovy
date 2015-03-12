package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

class ColumnsContext implements Context {
    private final JobManagement jobManagement
    List<Node> columnNodes = []

    ColumnsContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

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

    void configureProject() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.ConfigureProjectColumn')
    }

    void claim() {
        columnNodes << new Node(null, 'hudson.plugins.claim.ClaimColumn')
    }

    void lastBuildNode() {
        jobManagement.requireMinimumPluginVersion('build-node-column', '0.1')
        columnNodes << new Node(null, 'org.jenkins.plugins.column.LastBuildNodeColumn')
    }
}
