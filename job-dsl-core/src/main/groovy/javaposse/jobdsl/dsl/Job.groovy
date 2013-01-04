package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.*

/**
 * DSL Element representing a Jenkins Job
 *
 * @author jryan
 * @author aharmel-law
 */
public class Job {
    JobManagement jobManagement

    String name // Required
    String templateName = null // Optional
    List<WithXmlAction> withXmlActions = []

    // The idea here is that we'll let the helpers define their own methods, without polluting this class too much
    // TODO Use some methodMissing to do some sort of dynamic lookup
    @Delegate AuthorizationContextHelper helperAuthorization
    @Delegate ScmContextHelper helperScm
    @Delegate TriggerContextHelper helperTrigger
    @Delegate StepContextHelper helperStep
    @Delegate PublisherContextHelper helperPublisher
    @Delegate MultiScmContextHelper helperMultiscm
    @Delegate TopLevelHelper helperTopLevel

    public Job(JobManagement jobManagement) {
        this.jobManagement = jobManagement;
        helperAuthorization = new AuthorizationContextHelper(withXmlActions)
        helperScm = new ScmContextHelper(withXmlActions)
        helperMultiscm = new MultiScmContextHelper(withXmlActions)
        helperTrigger = new TriggerContextHelper(withXmlActions)
        helperStep = new StepContextHelper(withXmlActions)
        helperPublisher = new PublisherContextHelper(withXmlActions)
        helperTopLevel = new TopLevelHelper(withXmlActions)
    }

    /**
     * Creates a new job configuration, based on the job template referenced by the parameter and stores this.
     * @param templateName the name of the template upon which to base the new job
     * @return a new graph of groovy.util.Node objects, representing the job configuration structure
     * @throws JobTemplateMissingException
     */
    def using(String templateName) throws JobTemplateMissingException {
        if (this.templateName != null) {
            throw new RuntimeException('Can only use "using" once')
        }
        this.templateName = templateName
    }

    /**
     * Provide raw config.xml for direct manipulation. Provided as a StreamingMarkupBuilder
     *
     * Examples:
     *
     * <pre>
     * configure {
     *
     * }
     * </pre>
     * @param withXmlClosure
     * @return
     */
    def configure(Closure withXmlClosure) {
        withXmlActions.add( new WithXmlAction(withXmlClosure) )
    }

    def name(String name) {
        // TODO Validation
        this.name = name
    }

    def name(Closure nameClosure) {
        // TODO do we need a delegate?
        name(nameClosure.call().toString())
    }

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     * @return
     */
    public String getXml() {
        Node project = templateName==null?executeEmptyTemplate():executeUsing()

        // TODO check name field

        executeWithXmlActions(project)

        //new XmlNodePrinter(new PrintWriter(new FileWriter(new File('job.xml')))).print(project)

        def xmlOutput = new StringWriter()
        def xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), "    ")
        xmlNodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            quote = "'" // Use single quote for attributes
        }
        xmlNodePrinter.print(project)

        String configStr = xmlOutput.toString()
        //String configStr = XmlUtil.serialize(project)
        return configStr
    }

    void executeWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        // TODO Some Node magic to copy it at each phase, and then presenting a diff in the logs
        withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    // TODO record which templates are used to generate jobs, so that they can be connected to this job
    private executeUsing() {
        String configXml
        try {
            configXml = jobManagement.getConfig(templateName)
            if (configXml==null) {
                throw new JobConfigurationNotFoundException()
            }
        } catch (JobConfigurationNotFoundException jcnfex) {
            throw new JobTemplateMissingException(templateName)
        }

        def templateNode = new XmlParser().parse(new StringReader(configXml))

        // Clean up our own indication that a job is a template
        List<Node> seedJobProperties = templateNode.depthFirst().findAll { it.name() == 'javaposse.jobdsl.plugin.SeedJobsProperty' }
        seedJobProperties.each { Node node -> node.parent().remove(node) }

        return templateNode
    }

    private executeEmptyTemplate() {
        return new XmlParser().parse(new StringReader(emptyTemplate))
    }

    def emptyTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<project>
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
  <builders/>
  <publishers/>
  <buildWrappers/>
</project>
'''
}