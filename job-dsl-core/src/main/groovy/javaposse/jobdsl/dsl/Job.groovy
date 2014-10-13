package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.AuthorizationContextHelper
import javaposse.jobdsl.dsl.helpers.BuildParametersContextHelper
import javaposse.jobdsl.dsl.helpers.MavenHelper
import javaposse.jobdsl.dsl.helpers.BuildFlowHelper
import javaposse.jobdsl.dsl.helpers.MatrixHelper
import javaposse.jobdsl.dsl.helpers.MultiScmContextHelper
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContextHelper
import javaposse.jobdsl.dsl.helpers.step.StepContextHelper
import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.toplevel.TopLevelHelper
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContextHelper
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContextHelper

/**
 * DSL element representing a Jenkins job.
 */
class Job extends Item {
    JobManagement jobManagement

    String templateName = null // Optional
    JobType type = null // Required

    // The idea here is that we'll let the helpers define their own methods, without polluting this class too much
    @Delegate AuthorizationContextHelper helperAuthorization
    @Delegate TriggerContextHelper helperTrigger
    @Delegate WrapperContextHelper helperWrapper
    @Delegate StepContextHelper helperStep
    @Delegate PublisherContextHelper helperPublisher
    @Delegate MultiScmContextHelper helperMultiscm
    @Delegate TopLevelHelper helperTopLevel
    @Delegate MavenHelper helperMaven
    @Delegate BuildFlowHelper helperBuildFlow
    @Delegate BuildParametersContextHelper helperBuildParameters
    @Delegate MatrixHelper helperMatrix

    Job(JobManagement jobManagement, Map<String, Object> arguments=[:]) {
        this.jobManagement = jobManagement
        def typeArg = arguments['type'] ?: JobType.Freeform
        this.type = (typeArg instanceof JobType) ? typeArg : JobType.find(typeArg)

        // Helpers
        helperAuthorization = new AuthorizationContextHelper(withXmlActions, type)
        helperMultiscm = new MultiScmContextHelper(withXmlActions, type, jobManagement)
        helperTrigger = new TriggerContextHelper(withXmlActions, type, jobManagement)
        helperWrapper = new WrapperContextHelper(withXmlActions, type, jobManagement)
        helperStep = new StepContextHelper(withXmlActions, type, jobManagement)
        helperPublisher = new PublisherContextHelper(withXmlActions, type, jobManagement)
        helperTopLevel = new TopLevelHelper(withXmlActions, type, jobManagement)
        helperMaven = new MavenHelper(withXmlActions, type, jobManagement)
        helperBuildFlow = new BuildFlowHelper(withXmlActions, type)
        helperBuildParameters = new BuildParametersContextHelper(withXmlActions, type)
        helperMatrix = new MatrixHelper(withXmlActions, type)
    }

    /**
     * Creates a new job configuration, based on the job template referenced by the parameter and stores this.
     * @param templateName the name of the template upon which to base the new job
     * @return a new graph of groovy.util.Node objects, representing the job configuration structure
     * @throws JobTemplateMissingException
     */
    def using(String templateName) throws JobTemplateMissingException {
        Preconditions.checkState(this.templateName == null, 'Can only use "using" once')
        this.templateName = templateName
    }

    @Deprecated
    def name(Closure nameClosure) {
        jobManagement.logDeprecationWarning()
        name(nameClosure.call().toString())
    }

    def scm(Closure closure) {
        ScmContext context = new ScmContext(false, withXmlActions, jobManagement)
        AbstractContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            def scm = project / scm
            if (scm) {
                // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            // Assuming append the only child
            project << context.scmNode
        }
    }

    Node getNode() {
        Node project = templateName == null ? executeEmptyTemplate() : executeUsing()

        executeWithXmlActions(project)

        project
    }

    void executeWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    private executeUsing() {
        String configXml
        try {
            configXml = jobManagement.getConfig(templateName)
            if (configXml == null) {
                throw new JobConfigurationNotFoundException()
            }
        } catch (JobConfigurationNotFoundException jcnfex) {
            throw new JobTemplateMissingException(templateName)
        }

        def templateNode = new XmlParser().parse(new StringReader(configXml))

        if (type != getJobType(templateNode)) {
            throw new JobTypeMismatchException(name, templateName)
        }

        templateNode
    }

    private executeEmptyTemplate() {
        new XmlParser().parse(new StringReader(getTemplate(type)))
    }

    private String getTemplate(JobType type) {
        switch (type) {
            case JobType.Freeform: return emptyTemplate
            case JobType.BuildFlow: return emptyBuildFlowTemplate
            case JobType.Maven: return emptyMavenTemplate
            case JobType.Multijob: return emptyMultijobTemplate
            case JobType.Matrix: return emptyMatrixJobTemplate
        }
    }

    /**
     * Determines the job type from the given config XML.
     */
    private static JobType getJobType(Node node) {
        def nodeElement = node.name()
        JobType.values().find { it.elementName == nodeElement }
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

    def emptyBuildFlowTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
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

    def emptyMatrixJobTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
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
