package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.Permissions
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class JobTest extends Specification {
    private final resourcesDir = new File(getClass().getResource('/simple.dsl').toURI()).parentFile

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'construct a job manually (not from a DSL script)'() {
        setup:
        JobManagement jm = Mock()

        when:
        new Job(jm)

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
        job.xml

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
        def xml = job.xml

        then:
        _ * jm.getConfig('TMPL') >> minimalXml
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + minimalXml, xml
    }

    def 'load large template from file'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Job job = new Job(jm)

        when:
        job.using('config')
        String project = job.xml

        then:
        project.contains('<description>Description</description>')
    }

    def 'generate job from missing template - throws JobTemplateMissingException'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Job job = new Job(jm)

        when:
        job.using('TMPL-NOT_THERE')
        job.xml

        then:
        thrown(JobTemplateMissingException)
    }

    def 'run engine and ensure canRoam values'() {
        setup:
        JobManagement jm = new FileJobManagement(resourcesDir)
        Job job = new Job(jm)

        when:
        def projectRoaming = job.node

        then:
        // See that jobs can roam by default
        projectRoaming.canRoam[0].value() == ['true']

        when:
        job.label('Ubuntu')
        def projectLabelled = job.node

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
        !job.withXmlActions.empty
    }

    class JobParentConcrete extends JobParent {

        @Override
        Object run() {
            null // Used in tests
        }
    }

    def 'update Node using withXml'() {
        setup:
        Node project = new XmlParser().parse(new StringReader(minimalXml))
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
        def xml = job.xml

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + mavenXml, xml
    }

    def 'free-style job extends Maven template and fails to generate xml'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)

        when:
        job.using('TMPL')
        job.xml

        then:
        1 * jm.getConfig('TMPL') >> mavenXml
        thrown(JobTypeMismatchException)
    }

    def 'Maven job extends free-style template and fails to generate xml'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm, [type: JobType.Maven])

        when:
        job.using('TMPL')
        job.xml

        then:
        1 * jm.getConfig('TMPL') >> minimalXml
        thrown(JobTypeMismatchException)
    }

    def 'construct simple Build Flow job and generate xml from it'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm, [type: 'buildFlow'])

        when:
        def xml = job.xml

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + buildFlowXml, xml
    }

    def 'construct simple Matrix job and generate xml from it'() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm, [type: 'Matrix'])

        when:
        def xml = job.xml

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + matrixJobXml, xml
    }

    def 'call authorization'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.authorization {
            permission('hudson.model.Item.Configure:jill')
            permission('hudson.model.Item.Configure:jack')
        }

        then:
        NodeList permissions = job.node.properties[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }

    def 'call permission'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.permission('hudson.model.Item.Configure:jill')
        job.permission(Permissions.ItemRead, 'jack')
        job.permission('RunUpdate', 'joe')

        then:
        NodeList permissions = job.node.properties[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 3
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Read:jack'
        permissions[2].text() == 'hudson.model.Run.Update:joe'
    }

    def 'call parameters via helper'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.parameters {
            booleanParam('myBooleanParam', true)
        }

        then:
        with(job.node.properties[0].'hudson.model.ParametersDefinitionProperty'[0].parameterDefinitions[0]) {
            children()[0].name() == 'hudson.model.BooleanParameterDefinition'
            children()[0].name.text() == 'myBooleanParam'
            children()[0].defaultValue.text() == 'true'
        }
    }

    def 'call scm'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.scm {
            git {
                wipeOutWorkspace()
            }
        }

        then:
        job.node.scm[0].wipeOutWorkspace[0].text() == 'true'
    }

    def 'duplicate scm calls allowed with multiscm'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.multiscm {
            git('git://github.com/jenkinsci/jenkins.git')
            git('git://github.com/jenkinsci/job-dsl-plugin.git')
        }

        then:
        noExceptionThrown()
        job.node.scm[0].scms[0].scm.size() == 2
    }

    def 'call wrappers'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.wrappers {
            maskPasswords()
        }

        then:
        job.node.buildWrappers[0].children()[0].name() ==
                'com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper'
    }

    def 'call triggers'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.triggers {
            scm('2 3 * * * *')
        }

        then:
        job.node.triggers[0].'hudson.triggers.SCMTrigger'[0].spec[0].text() == '2 3 * * * *'
    }

    def 'no steps for Maven jobs'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: 'Maven'])

        when:
        job.steps {
        }

        then:
        thrown(IllegalStateException)
    }

    def 'call steps'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.steps {
            shell('ls')
        }

        then:
        job.node.builders[0].'hudson.tasks.Shell'[0].command[0].text() == 'ls'
    }

    def 'call publishers'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.publishers {
            chucknorris()
        }

        then:
        job.node.publishers[0].'hudson.plugins.chucknorris.CordellWalkerRecorder'[0].factGenerator[0].text() == ''
    }

    def 'cannot create Build Flow for free style jobs'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.buildFlow('build block')

        then:
        thrown(IllegalStateException)
    }

    def 'buildFlow constructs xml'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: JobType.BuildFlow])

        when:
        job.buildFlow('build Flow Block')

        then:
        job.node.dsl.size() == 1
        job.node.dsl[0].value() == 'build Flow Block'
    }

    def 'cannot run combinationFilter for free style jobs'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.combinationFilter('LABEL1 == "TEST"')

        then:
        thrown(IllegalStateException)
    }

    def 'combinationFilter constructs xml'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: JobType.Matrix])

        when:
        job.combinationFilter('LABEL1 == "TEST"')

        then:
        job.node.combinationFilter.size() == 1
        job.node.combinationFilter[0].value() == 'LABEL1 == "TEST"'
    }

    def 'cannot set runSequentially for free style jobs'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.runSequentially()

        then:
        thrown(IllegalStateException)
    }

    def 'runSequentially constructs xml'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: JobType.Matrix])

        when:
        job.runSequentially(false)

        then:
        job.node.executionStrategy.runSequentially.size() == 1
        job.node.executionStrategy.runSequentially[0].value() == false
    }

    def 'cannot set touchStoneFilter for free style jobs'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        thrown(IllegalStateException)
    }

    def 'touchStoneFilter constructs xml'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: JobType.Matrix])

        when:
        job.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        with(job.node.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'UNSTABLE'
            touchStoneResultCondition[0].color[0].value() == 'YELLOW'
            touchStoneResultCondition[0].ordinal[0].value() == 1
        }

        when:
        job.touchStoneFilter('LABEL1 == "TEST"', false)

        then:
        with(job.node.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'STABLE'
            touchStoneResultCondition[0].color[0].value() == 'BLUE'
            touchStoneResultCondition[0].ordinal[0].value() == 0
        }
    }

    def 'cannot run axis for free style jobs'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm)

        when:
        job.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        thrown(IllegalStateException)
    }

    def 'can set axis'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: JobType.Matrix])

        when:
        job.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        job.node.axes.size() == 1
        job.node.axes[0].children().size() == 1
        job.node.axes[0].children()[0].name() == 'hudson.matrix.LabelAxis'
    }

    def 'axes configure block constructs xml'() {
        setup:
        JobManagement jm = Mock(JobManagement)
        Job job = new Job(jm, [type: JobType.Matrix])

        when:
        job.axes {
            configure { axes ->
                axes << 'FooAxis'()
            }
        }

        then:
        job.node.axes.size() == 1
        job.node.axes[0].children().size() == 1
        job.node.axes[0].children()[0].name() == 'FooAxis'
    }

    private final minimalXml = '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>
'''

    private final mavenXml = '''
<maven2-moduleset>
    <actions/>
    <description></description>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <scm class="hudson.scm.NullSCM"/>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers class="vector"/>
    <concurrentBuild>false</concurrentBuild>
    <aggregatorStyleBuild>true</aggregatorStyleBuild>
    <incrementalBuild>false</incrementalBuild>
    <perModuleEmail>false</perModuleEmail>
    <ignoreUpstremChanges>true</ignoreUpstremChanges>
    <archivingDisabled>false</archivingDisabled>
    <resolveDependencies>false</resolveDependencies>
    <processPlugins>false</processPlugins>
    <mavenValidationLevel>-1</mavenValidationLevel>
    <runHeadless>false</runHeadless>
    <publishers/>
    <buildWrappers/>
</maven2-moduleset>
'''

    private final buildFlowXml = '''
<com.cloudbees.plugins.flow.BuildFlow>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers class="vector"/>
  <concurrentBuild>false</concurrentBuild>
  <builders/>
  <publishers/>
  <buildWrappers/>
  <icon/>
  <dsl></dsl>
</com.cloudbees.plugins.flow.BuildFlow>
'''

private final matrixJobXml = '''
<matrix-project>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers class="vector"/>
  <concurrentBuild>false</concurrentBuild>
  <axes/>
  <builders/>
  <publishers/>
  <buildWrappers/>
  <executionStrategy class="hudson.matrix.DefaultMatrixExecutionStrategyImpl">
    <runSequentially>false</runSequentially>
  </executionStrategy>
</matrix-project>
'''
}
