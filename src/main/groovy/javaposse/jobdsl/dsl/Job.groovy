package javaposse.jobdsl.dsl

import groovy.util.Node;
import groovy.xml.XmlUtil
import java.io.StringReader

public class Job {
    String name // Required
    Node project
    JobManagement jobManagement

    public Job(JobManagement jobManagement) {
        this.jobManagement = jobManagement;
    }

    def using(String templateName) {
        String configXml = jobManagement.getConfig(templateName)
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