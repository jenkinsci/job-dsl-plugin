package javaposse.jobdsl.dsl.additional

import javaposse.jobdsl.dsl.ItemType
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class PromotionTest extends Specification {
    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'update Promotions Nodes using withXml'() {
        setup:
        Node project = new XmlParser().parse(new StringReader(promotionXml))
        Promotion job = new Promotion(null)

        when: 'Simple update'
        job.configure { Node node ->
            node / 'actions' {
                description('Test Description')
            }
        }
        job.executeWithXmlActions(project)

        then:
        project.actions[0].description.text() == 'Test Description'
    }

    def 'construct simple Promotions and generate xmls from it'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm, [type: 'maven'])
        Promotion promotion = new Promotion('dev')

        when:
        job.additionalConfigs << promotion
        def configs = job.additionalConfigs

        def devConfig
        for (AdditionalXmlConfig config : configs) {
            if (config.configType == ItemType.ADDITIONAL && config.name == 'dev') {
                devConfig = config
            }
        }

        then:
        assertXMLEqual devConfig.xml, promotionXml
    }

    private final promotionXml = '''
<hudson.plugins.promoted__builds.PromotionProcess plugin="promoted-builds@2.15">
  <actions/>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>false</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <conditions/>
  <icon/>
  <buildSteps/>
</hudson.plugins.promoted__builds.PromotionProcess>
'''
}
