package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class PipelineAggregatorViewSpec extends Specification {
    private static final String DEFAULT_XML = '''<?xml version="1.1" encoding="UTF-8"?>
        <com.ooyala.jenkins.plugins.pipelineaggregatorview.PipelineAggregator>
            <filterExecutors>false</filterExecutors>
            <filterQueue>false</filterQueue>
            <properties class="hudson.model.View$PropertyList"/>
            <fontSize>16</fontSize>
            <buildHistorySize>16</buildHistorySize>
            <refreshInterval>15</refreshInterval>
            <useCondensedTables>false</useCondensedTables>
            <onlyLastBuild>false</onlyLastBuild>
            <useScrollingCommits>false</useScrollingCommits>
            <showCommitInfo>true</showCommitInfo>
            <showBuildNumber>true</showBuildNumber>
            <showBuildTime>true</showBuildTime>
            <showBuildDuration>true</showBuildDuration>
        </com.ooyala.jenkins.plugins.pipelineaggregatorview.PipelineAggregator>'''

    private final JobManagement jobManagement = Mock(JobManagement)
    private final PipelineAggregatorView view = new PipelineAggregatorView(jobManagement, 'test')

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'defaults'() {
        expect:
        compareXML(DEFAULT_XML, view.xml).similar()
    }

    def 'buildHistorySize'() {
        when:
        view.buildHistorySize(10)

        then:
        Node root = view.node
        root.buildHistorySize.text() == '10'
    }

    def 'buildHistorySize zero'() {
        when:
        view.buildHistorySize(0)

        then:
        thrown(DslScriptException)
    }

    def 'buildHistorySize negative'() {
        when:
        view.buildHistorySize(-1)

        then:
        thrown(DslScriptException)
    }

    def 'filterRegex'() {
        when:
        view.filterRegex('.*Pipeline')

        then:
        Node root = view.node
        root.filterRegex.text() == '.*Pipeline'
    }

    def 'filterRegex empty'() {
        when:
        view.filterRegex('')

        then:
        thrown(DslScriptException)
    }

    def 'filterRegex null'() {
        when:
        view.filterRegex(null)

        then:
        thrown(DslScriptException)
    }

    def 'fontSize'() {
        when:
        view.fontSize(10)

        then:
        Node root = view.node
        root.fontSize.text() == '10'
    }

    def 'fontSize zero'() {
        when:
        view.fontSize(0)

        then:
        thrown(DslScriptException)
    }

    def 'fontSize negative'() {
        when:
        view.fontSize(-1)

        then:
        thrown(DslScriptException)
    }

    def 'onlyLastBuild'() {
        when:
        view.onlyLastBuild(true)

        then:
        Node root = view.node
        root.onlyLastBuild.text() == 'true'
    }

    def 'onlyLastBuild no arguments'() {
        when:
        view.onlyLastBuild()

        then:
        Node root = view.node
        root.onlyLastBuild.text() == 'true'
    }

    def 'useCondensedTables'() {
        when:
        view.useCondensedTables(true)

        then:
        Node root = view.node
        root.useCondensedTables.text() == 'true'
    }

    def 'useCondensedTables no arguments'() {
        when:
        view.useCondensedTables()

        then:
        Node root = view.node
        root.useCondensedTables.text() == 'true'
    }

    def 'useScrollingCommits'() {
        when:
        view.useScrollingCommits(true)

        then:
        Node root = view.node
        root.useScrollingCommits.text() == 'true'
    }

    def 'useScrollingCommits no arguments'() {
        when:
        view.useScrollingCommits()

        then:
        Node root = view.node
        root.useScrollingCommits.text() == 'true'
    }
}
