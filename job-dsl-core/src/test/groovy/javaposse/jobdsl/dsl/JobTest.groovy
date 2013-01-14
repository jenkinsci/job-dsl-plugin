package javaposse.jobdsl.dsl

import spock.lang.*
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

import java.util.concurrent.atomic.AtomicBoolean

class JobTest extends Specification {
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
        println new File('src/test/resources').absolutePath
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

    def violations(job, typeName, minNum, maxNum, unstableNum, filenamePattern = null) {
        job.configure { project ->
            def violationsConfig = project / publishers / 'hudson.plugins.violations.ViolationsPublisher' / 'config'

            /* these are the lines relevant to the discussion */
            def typeConfigsNode = violationsConfig / typeConfigs
            def typeEntry = typeConfigsNode / entry
            /* --- */

            typeEntry / string(typeName)
            def typeConfig = typeEntry / 'hudson.plugins.violations.TypeConfig'
            typeConfig / type(typeName)
            typeConfig / min(minNum.toString())
            typeConfig / max(maxNum.toString())
            typeConfig / unstable(unstableNum.toString())
            typeConfig / usePattern(filenamePattern ? "true" : "false")
            typeConfig / pattern(filenamePattern)
        }
    }

    class JobParentConcrete extends JobParent {

        @Override
        Object run() {
            return null // Used in tests
        }
    }

    def 'typeConfigs not appearing correctly'() {
        setup:
        final Node project = new XmlParser().parse(new StringReader(minimalXml))
        def parent = new JobParentConcrete()

        when:
        def job = parent.job {
            name "Tools-jshint-dsl"
            violations(delegate, "jslint", 10, 11, 10, "test-reports/*.xml")
        }
        job.executeWithXmlActions(project)

        then:
        def configNode = project.publishers[0].'hudson.plugins.violations.ViolationsPublisher'[0].config[0]
        configNode.typeConfigs.size() == 1
        def typeEntryNode = configNode.typeConfigs[0].entry[0]
        typeEntryNode.string[0].value() == 'jslint'
        def typeConfigNode = typeEntryNode.'hudson.plugins.violations.TypeConfig'[0]
        typeConfigNode.type[0].value() == 'jslint'
        typeConfigNode.min[0].value() == '10'
        typeConfigNode.max[0].value() == '11'
        typeConfigNode.unstable[0].value() == '10'
        typeConfigNode.usePattern[0].value() == 'true'
        typeConfigNode.pattern[0].value() == 'test-reports/*.xml'
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

    final minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

}
