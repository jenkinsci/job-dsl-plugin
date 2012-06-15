package javaposse.jobdsl.dsl

import groovy.util.Node;
import groovy.xml.XmlUtil
import java.io.StringReader

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
    Closure configureClosure = null // Optional
    List<WithXmlAction> withXmlActions = []

    public Job(JobManagement jobManagement) {
        this.jobManagement = jobManagement;
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

    @Deprecated
    def configure(Closure configureClosure) {
        if (this.configureClosure != null) {
            throw new RuntimeException('Can only use "configure" once')
        }
        this.configureClosure = configureClosure
    }

    /**
     * Provide raw config.xml for direct manipulation. Provided as a StreamingMarkupBuilder
     *
     * Examples:
     *
     * <pre>
     * withXml {
     *
     * }
     * </pre>
     * @param withXmlClosure
     * @return
     */
    def withXml(Closure withXmlClosure) {
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

        if (configureClosure != null) {
            executeConfigure(project)
        }

        //new XmlNodePrinter(new PrintWriter(new FileWriter(new File('job.xml')))).print(project)

        String configStr = XmlUtil.serialize(project)
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

        return new XmlParser().parse(new StringReader(configXml))
    }

    private executeEmptyTemplate() {
        return new XmlParser().parse(new StringReader(emptyTemplate))
    }

    private executeConfigure(Node project) {
        NodeDelegate nd = new NodeDelegate(project)
        configureClosure.delegate = nd
        configureClosure.resolveStrategy = Closure.OWNER_FIRST // so that outside variables get resolved first
        // make Node Delegate available, so that it can be passed to other methods
        configureClosure.call(nd)
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
}