package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.AuthorizationContextHelper
import javaposse.jobdsl.dsl.helpers.BuildParametersContextHelper
import javaposse.jobdsl.dsl.helpers.MavenHelper
import javaposse.jobdsl.dsl.helpers.MultiScmContextHelper
import javaposse.jobdsl.dsl.helpers.ScmContextHelper
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContextHelper
import javaposse.jobdsl.dsl.helpers.step.StepContextHelper
import javaposse.jobdsl.dsl.helpers.toplevel.TopLevelHelper
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContextHelper
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContextHelper

/**
 * DSL Element representing a Jenkins Job
 *
 * @author jryan
 * @author aharmel-law
 */
public class Job extends JobItem {

    JobType type = null // Required

    // The idea here is that we'll let the helpers define their own methods, without polluting this class too much
    // TODO Use some methodMissing to do some sort of dynamic lookup
    @Delegate AuthorizationContextHelper helperAuthorization
    @Delegate ScmContextHelper helperScm
    @Delegate TriggerContextHelper helperTrigger
    @Delegate WrapperContextHelper helperWrapper
    @Delegate StepContextHelper helperStep
    @Delegate PublisherContextHelper helperPublisher
    @Delegate MultiScmContextHelper helperMultiscm
    @Delegate TopLevelHelper helperTopLevel
    @Delegate MavenHelper helperMaven
    @Delegate BuildParametersContextHelper helperBuildParameters

    public Job(JobManagement jobManagement, Map<String, Object> arguments=[:], JobItem parent=null) {
        super(jobManagement, parent)
        def typeArg = arguments['type']?:JobType.Freeform
        this.type = (typeArg instanceof JobType)?typeArg:JobType.find(typeArg)

        // Helpers

        helperAuthorization = new AuthorizationContextHelper(super.withXmlActions, type)
        helperScm = new ScmContextHelper(super.withXmlActions, type, jobManagement)
        helperMultiscm = new MultiScmContextHelper(super.withXmlActions, type, jobManagement)
        helperTrigger = new TriggerContextHelper(super.withXmlActions, type)
        helperWrapper = new WrapperContextHelper(super.withXmlActions, type, jobManagement)
        helperStep = new StepContextHelper(super.withXmlActions, type)
        helperPublisher = new PublisherContextHelper(super.withXmlActions, type)
        helperTopLevel = new TopLevelHelper(super.withXmlActions, type)
        helperMaven = new MavenHelper(super.withXmlActions, type)
        helperBuildParameters = new BuildParametersContextHelper(super.withXmlActions, type)
    }

    @Override
    protected void matchTemplate(Node templateNode) {
        if (type != getJobType(templateNode)) {
            throw new JobTypeMismatchException(name, templateName);
        }
    }

    @Override
    protected String getTemplate() {
        return getTemplate(type);
    }

    private String getTemplate(JobType type) {
        // TODO Move this logic to the JobType Enum
        switch(type) {
            case JobType.Freeform: return emptyTemplate
            case JobType.Maven: return emptyMavenTemplate
            case JobType.Multijob: return emptyMultijobTemplate
       }
    }

    /**
     * Determines the job type from the given config XML.
     */
    private static JobType getJobType(Node node) {
        def nodeElement = node.name()
        return JobType.values().find { it.elementName == nodeElement }
    }

    def emptyTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<project>
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
</project>
'''

    def emptyMavenTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
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

    def emptyMultijobTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<com.tikal.jenkins.plugins.multijob.MultiJobProject plugin="jenkins-multijob-plugin@1.8">
  <actions/>
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
  <builders/>
  <publishers/>
  <buildWrappers/>
</com.tikal.jenkins.plugins.multijob.MultiJobProject>
'''

}
