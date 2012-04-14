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
    Node project
    String templateName

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
        String configXml
        try {
            configXml = jobManagement.getConfig(templateName)
        } catch (JobConfigurationNotFoundException jcnfex) {
            throw new JobTemplateMissingException(templateName)
        }

        // Save for late, when constructing GeneratedJob
        this.templateName = templateName

        // TODO record which templates are used to generate jobs, so that they can be connected to this job
        project = new XmlParser().parse(new StringReader(configXml))
    }

    def configure(Closure configureClosure) {
        configureClosure.delegate = new NodeDelegate(project)
        // make Node Delegate available, so that it can be passed to other methods
        configureClosure.call(configureClosure.delegate) 
    }

    public String getXml() {
        //new XmlNodePrinter(new PrintWriter(new FileWriter(new File('job.xml')))).print(project)

        String configStr = XmlUtil.serialize(project)
        return configStr
    }

    def name(String name) {
        this.name = name
    }

    def name(Closure nameClosure) {
        // TODO do we need a delegate?
        this.name = nameClosure.call()
    }
}