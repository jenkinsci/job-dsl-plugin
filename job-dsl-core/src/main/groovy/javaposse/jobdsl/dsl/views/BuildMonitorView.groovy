package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement

class BuildMonitorView extends ListView {
    BuildMonitorView(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    protected String getTemplate() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames/>
    <jobFilters/>
    <columns/>
    <recurse>false</recurse>
    <order class="com.smartcodeltd.jenkinsci.plugins.buildmonitor.order.ByName"/>
</com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView>'''
    }
}
