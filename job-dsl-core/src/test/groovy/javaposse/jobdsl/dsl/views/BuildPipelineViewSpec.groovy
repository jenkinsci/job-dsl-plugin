package javaposse.jobdsl.dsl.views

import spock.lang.Specification

import static javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle.Lightbox
import static javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle.NewWindow
import static javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle.ThisWindow
import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class BuildPipelineViewSpec extends Specification {
    BuildPipelineView view = new BuildPipelineView()

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        setIgnoreWhitespace(true)
        compareXML(defaultXml, xml).similar()
    }

    def 'displayedBuilds'() {
        when:
        view.displayedBuilds(5)

        then:
        Node root = view.node
        root.noOfDisplayedBuilds.size() == 1
        root.noOfDisplayedBuilds[0].text() == '5'
    }

    def 'displayedBuilds zero'() {
        when:
        view.displayedBuilds(0)

        then:
        thrown(IllegalArgumentException)
    }

    def 'displayedBuilds negative'() {
        when:
        view.displayedBuilds(-12)

        then:
        thrown(IllegalArgumentException)
    }

    def 'title'() {
        when:
        view.title('foo')

        then:
        Node root = view.node
        root.buildViewTitle.size() == 1
        root.buildViewTitle[0].text() == 'foo'
    }

    def 'title null'() {
        when:
        view.title(null)

        then:
        Node root = view.node
        root.buildViewTitle.size() == 1
        root.buildViewTitle[0].text() == ''
    }

    def 'selectedJob'() {
        when:
        view.selectedJob('foo')

        then:
        Node root = view.node
        root.selectedJob.size() == 1
        root.selectedJob[0].text() == 'foo'
    }

    def 'selectedJob null'() {
        when:
        view.selectedJob(null)

        then:
        thrown(NullPointerException)
    }

    def 'consoleOutputLinkStyle No Arg'() {
        when:
        view.consoleOutputLinkStyle()

        then:
        thrown(NullPointerException)
    }

    def 'consoleOutputLinkStyle Lightbox'() {
        when:
        view.consoleOutputLinkStyle(Lightbox)

        then:
        Node root = view.node
        root.consoleOutputLinkStyle.size() == 1
        root.consoleOutputLinkStyle[0].text() == 'Lightbox'
    }

    def 'consoleOutputLinkStyle New Window'() {
        when:
        view.consoleOutputLinkStyle(NewWindow)

        then:
        Node root = view.node
        root.consoleOutputLinkStyle.size() == 1
        root.consoleOutputLinkStyle[0].text() == 'New Window'
    }

    def 'consoleOutputLinkStyle This Window'() {
        when:
        view.consoleOutputLinkStyle(ThisWindow)

        then:
        Node root = view.node
        root.consoleOutputLinkStyle.size() == 1
        root.consoleOutputLinkStyle[0].text() == 'This Window'
    }

    def 'consoleOutputLinkStyle null'() {
        when:
        view.consoleOutputLinkStyle(null)

        then:
        thrown(NullPointerException)
    }

    def 'cssUrl'() {
        when:
        view.customCssUrl('foo')

        then:
        Node root = view.node
        root.cssUrl.size() == 1
        root.cssUrl[0].text() == 'foo'
    }

    def 'cssUrl null'() {
        when:
        view.customCssUrl(null)

        then:
        Node root = view.node
        root.cssUrl.size() == 1
        root.cssUrl[0].text() == ''
    }

    def 'triggerOnlyLatestJob'() {
        when:
        view.triggerOnlyLatestJob(true)

        then:
        Node root = view.node
        root.triggerOnlyLatestJob.size() == 1
        root.triggerOnlyLatestJob[0].text() == 'true'
    }

    def 'triggerOnlyLatestJob no arguments'() {
        when:
        view.triggerOnlyLatestJob()

        then:
        Node root = view.node
        root.triggerOnlyLatestJob.size() == 1
        root.triggerOnlyLatestJob[0].text() == 'true'
    }

    def 'alwaysAllowManualTrigger'() {
        when:
        view.alwaysAllowManualTrigger(true)

        then:
        Node root = view.node
        root.alwaysAllowManualTrigger.size() == 1
        root.alwaysAllowManualTrigger[0].text() == 'true'
    }

    def 'alwaysAllowManualTrigger no arguments'() {
        when:
        view.alwaysAllowManualTrigger()

        then:
        Node root = view.node
        root.alwaysAllowManualTrigger.size() == 1
        root.alwaysAllowManualTrigger[0].text() == 'true'
    }

    def 'showPipelineParameters'() {
        when:
        view.showPipelineParameters(true)

        then:
        Node root = view.node
        root.showPipelineParameters.size() == 1
        root.showPipelineParameters[0].text() == 'true'
    }

    def 'showPipelineParameters no arguments'() {
        when:
        view.showPipelineParameters()

        then:
        Node root = view.node
        root.showPipelineParameters.size() == 1
        root.showPipelineParameters[0].text() == 'true'
    }

    def 'showPipelineParametersInHeaders'() {
        when:
        view.showPipelineParametersInHeaders(true)

        then:
        Node root = view.node
        root.showPipelineParametersInHeaders.size() == 1
        root.showPipelineParametersInHeaders[0].text() == 'true'
    }

    def 'showPipelineParametersInHeaders no arguments'() {
        when:
        view.showPipelineParametersInHeaders()

        then:
        Node root = view.node
        root.showPipelineParametersInHeaders.size() == 1
        root.showPipelineParametersInHeaders[0].text() == 'true'
    }

    def 'refreshFrequency'() {
        when:
        view.refreshFrequency(5)

        then:
        Node root = view.node
        root.refreshFrequency.size() == 1
        root.refreshFrequency[0].text() == '5'
    }

    def 'refreshFrequency zero'() {
        when:
        view.refreshFrequency(0)

        then:
        thrown(IllegalArgumentException)
    }

    def 'refreshFrequency negative'() {
        when:
        view.refreshFrequency(-12)

        then:
        thrown(IllegalArgumentException)
    }

    def 'showPipelineDefinitionHeader'() {
        when:
        view.showPipelineDefinitionHeader(true)

        then:
        Node root = view.node
        root.showPipelineDefinitionHeader.size() == 1
        root.showPipelineDefinitionHeader[0].text() == 'true'
    }

    def 'showPipelineDefinitionHeader no arguments'() {
        when:
        view.showPipelineDefinitionHeader()

        then:
        Node root = view.node
        root.showPipelineDefinitionHeader.size() == 1
        root.showPipelineDefinitionHeader[0].text() == 'true'
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <noOfDisplayedBuilds>1</noOfDisplayedBuilds>
    <buildViewTitle/>
    <consoleOutputLinkStyle>Lightbox</consoleOutputLinkStyle>
    <cssUrl/>
    <triggerOnlyLatestJob>false</triggerOnlyLatestJob>
    <alwaysAllowManualTrigger>false</alwaysAllowManualTrigger>
    <showPipelineParameters>false</showPipelineParameters>
    <showPipelineParametersInHeaders>false</showPipelineParametersInHeaders>
    <refreshFrequency>3</refreshFrequency>
    <showPipelineDefinitionHeader>false</showPipelineDefinitionHeader>
</au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView>'''
}
