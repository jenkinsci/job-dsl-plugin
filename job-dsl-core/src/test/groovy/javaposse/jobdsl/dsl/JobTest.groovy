package javaposse.jobdsl.dsl

import spock.lang.*
import org.custommonkey.xmlunit.XMLUnit
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

import java.util.concurrent.atomic.AtomicBoolean

class JobTest extends Specification {
    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'construct a job manually (not from a DSL script)'() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)

        then:
        notThrown(Exception)
    }

    def 'set name on a manually constructed job'() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)
        job.name = 'NAME'

        then:
        job.name == 'NAME'
    }

    def 'load an empty template from a manually constructed job'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)

        when:
        job.using('TMPL')
        job.getXml()

        then:
        1 * jm.getConfig('TMPL') >> minimalXml
    }

    def 'load an empty template from a manually constructed job and generate xml from it'() {
        setup:
        JobManagement jm = Mock()
        //jm.getConfig("TMPL") >> minimalXml
        Job job = new Job(jm)

        when:
        job.using('TMPL')
        def xml = job.getXml()

        then:
        _ * jm.getConfig('TMPL') >> minimalXml
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + minimalXml, xml
    }

    def 'load large template from file'() {
        setup:
        JobManagement jm = new FileJobManagement(new File('src/test/resources'))
        Job job = new Job(jm)

        when:
        job.using("config")
        String project = job.getXml()

        then:
        // assertXpathExists('/description', project) // java.lang.NoSuchMethodError: org.apache.xpath.XPathContext
        project.contains('<description>Description</description>')
    }

    def 'generate job from missing template - throws JobTemplateMissingException'() {
        setup:
        JobManagement jm = new FileJobManagement(new File("src/test/resources"))
        Job job = new Job(jm)

        when:
        job.using('TMPL-NOT_THERE')
        job.getXml()

        then:
        thrown(JobTemplateMissingException)
    }

    def 'run engine and ensure canRoam values'() {
        setup:
        JobManagement jm = new FileJobManagement(new File('src/test/resources'))
        Job job = new Job(jm)

        when:
        def projectRoaming = job.getNode()

        then:
        // See that jobs can roam by default
        projectRoaming.canRoam[0].value() == ['true']

        when:
        job.label('Ubuntu')
        def projectLabelled = job.getNode()

        then:
        projectLabelled.canRoam[0].value() == 'false'
    }

    def 'create withXml blocks'() {
        setup:
        Job job = new Job(null)

        when:
        job.configure { Node node ->
            // Not going to actually execute this
        }

        job.configure { Node node ->
            // Not going to actually execute this
        }

        then:
        !job.withXmlActions.isEmpty()

        when:
        job.configure("Not a closure")

        then:
        thrown(MissingMethodException)
    }

    class JobParentConcrete extends JobParent {

        @Override
        Object run() {
            return null // Used in tests
        }
    }

    def 'update Node using withXml'() {
        setup:
        final Node project = new XmlParser().parse(new StringReader(minimalXml))
        Job job = new Job(null)
        AtomicBoolean boolOutside = new AtomicBoolean(true)

        when: 'Simple update'
        job.configure { Node node ->
            node.description[0].value = 'Test Description'
        }
        job.executeWithXmlActions(project)

        then:
        project.description[0].text() == 'Test Description'

        when: 'Update using variable from outside scope'
        job.configure { Node node ->
            node.keepDependencies[0].value = boolOutside.get()
        }
        job.executeWithXmlActions(project)

        then:
        project.keepDependencies[0].text() == 'true'

        then:
        project.description[0].text() == 'Test Description'

        when: 'Change value on outside scope variable to ensure closure is being run again'
        boolOutside.set(false)
        job.executeWithXmlActions(project)

        then:
        project.keepDependencies[0].text() == 'false'

        when: 'Append node'
        job.configure { Node node ->
            def actions = node.actions[0]
            actions.appendNode('action', 'Make Breakfast')
        }
        job.executeWithXmlActions(project)

        then:
        project.actions[0].children().size() == 1

        when: 'Execute withXmlActions again'
        job.executeWithXmlActions(project)

        then:
        project.actions[0].children().size() == 2
    }

    def 'construct simple Maven job and generate xml from it'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm, [type: 'maven'])

        when:
        def xml = job.getXml()

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + mavenXml, xml
    }

    final minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

    final mavenXml = '''
<maven2-moduleset>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <scm class="hudson.scm.NullSCM"/>
    <canRoam>false</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>true</perModuleEmail>
    <ignoreUpstremChanges>false</ignoreUpstremChanges>
    <archivingDisabled>false</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>false</runHeadless>
    <publishers/>
    <buildWrappers/>
</maven2-moduleset>
'''
}
