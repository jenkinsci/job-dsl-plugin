package javaposse.jobdsl.dsl.views

import groovy.text.SimpleTemplateEngine
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.DeliveryPipelineView.Sorting
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class DeliveryPipelineViewSpec extends Specification {
    private static final String DEFAULT_XML = '''<?xml version='1.0' encoding='UTF-8'?>
<se.diabol.jenkins.pipeline.DeliveryPipelineView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <noOfPipelines>3</noOfPipelines>
    <showAggregatedPipeline>false</showAggregatedPipeline>
    <noOfColumns>1</noOfColumns>
    <sorting>none</sorting>
    <showAvatars>false</showAvatars>
    <updateInterval>2</updateInterval>
    <showChanges>false</showChanges>
    <allowManualTriggers>false</allowManualTriggers>
</se.diabol.jenkins.pipeline.DeliveryPipelineView>'''

    private static final String SORTING_XML_TEMPLATE = '''<?xml version='1.0' encoding='UTF-8'?>
<se.diabol.jenkins.pipeline.DeliveryPipelineView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View\\$PropertyList"/>
    <noOfPipelines>3</noOfPipelines>
    <showAggregatedPipeline>false</showAggregatedPipeline>
    <noOfColumns>1</noOfColumns>
    <sorting>$xmlValue</sorting>
    <showAvatars>false</showAvatars>
    <updateInterval>2</updateInterval>
    <showChanges>false</showChanges>
    <allowManualTriggers>false</allowManualTriggers>
</se.diabol.jenkins.pipeline.DeliveryPipelineView>'''

    private static final String ALL_OPTIONS_XML = '''<?xml version='1.0' encoding='UTF-8'?>
<se.diabol.jenkins.pipeline.DeliveryPipelineView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <noOfPipelines>5</noOfPipelines>
    <showAggregatedPipeline>true</showAggregatedPipeline>
    <noOfColumns>2</noOfColumns>
    <sorting>se.diabol.jenkins.pipeline.sort.NameComparator</sorting>
    <showAvatars>true</showAvatars>
    <updateInterval>60</updateInterval>
    <showChanges>true</showChanges>
    <allowManualTriggers>true</allowManualTriggers>
    <showTotalBuildTime>true</showTotalBuildTime>
    <allowRebuild>true</allowRebuild>
    <allowPipelineStart>true</allowPipelineStart>
    <showDescription>true</showDescription>
    <showPromotions>true</showPromotions>
    <pagingEnabled>true</pagingEnabled>
    <showTestResults>true</showTestResults>
    <showStaticAnalysisResults>true</showStaticAnalysisResults>
    <linkRelative>true</linkRelative>
    <linkToConsoleLog>true</linkToConsoleLog>
    <theme>foo</theme>
    <componentSpecs>
        <se.diabol.jenkins.pipeline.DeliveryPipelineView_-ComponentSpec>
            <name>test</name>
            <firstJob>compile-a</firstJob>
        </se.diabol.jenkins.pipeline.DeliveryPipelineView_-ComponentSpec>
    </componentSpecs>
    <regexpFirstJobs>
        <se.diabol.jenkins.pipeline.DeliveryPipelineView_-RegExpSpec>
            <regexp>compile-(.*)</regexp>
        </se.diabol.jenkins.pipeline.DeliveryPipelineView_-RegExpSpec>
    </regexpFirstJobs>
</se.diabol.jenkins.pipeline.DeliveryPipelineView>'''

    private final JobManagement jobManagement = Mock(JobManagement)
    private final DeliveryPipelineView view = new DeliveryPipelineView(jobManagement, 'test')

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        compareXML(DEFAULT_XML, xml).similar()
    }

    def 'sorting'(Sorting value, String xmlValue) {
        when:
        view.sorting(value)

        then:
        String xml = new SimpleTemplateEngine().createTemplate(SORTING_XML_TEMPLATE).make(xmlValue: xmlValue)
        compareXML(xml, view.xml).similar()

        where:
        value                 | xmlValue
        null                  | 'none'
        Sorting.NONE          | 'none'
        Sorting.TITLE         | 'se.diabol.jenkins.pipeline.sort.NameComparator'
        Sorting.LAST_ACTIVITY | 'se.diabol.jenkins.pipeline.sort.LatestActivityComparator'
        Sorting.FAILED_FIRST  | 'se.diabol.jenkins.pipeline.sort.FailedJobComparator'
    }

    def 'all options'() {
        when:
        view.with {
            pipelineInstances(5)
            showAggregatedPipeline()
            columns(2)
            sorting(Sorting.TITLE)
            showAvatars()
            updateInterval(60)
            showChangeLog()
            enableManualTriggers()
            showTotalBuildTime()
            allowRebuild()
            allowPipelineStart()
            showDescription()
            showPromotions()
            enablePaging()
            showTestResults()
            showStaticAnalysisResults()
            useRelativeLinks()
            linkToConsoleLog()
            useTheme('foo')

            pipelines {
                component('test', 'compile-a')
                regex(/compile-(.*)/)
            }
        }

        then:
        compareXML(ALL_OPTIONS_XML, view.xml).similar()
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.3')
    }
}
