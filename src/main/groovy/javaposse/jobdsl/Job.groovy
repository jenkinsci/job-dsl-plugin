package javaposse.jobdsl

import groovy.util.Node;
import groovy.xml.XmlUtil
import java.io.StringReader

public class Job {
    String name // Required
    Node project
    JobManagement jobManagement

    public Job(JobManagement jobManagement) {
        super();
        this.jobManagement = jobManagement;
    }

    def using(String templateName) {
        String configXml = jobManagement.getConfig(templateName)
        // TODO record which templates are used to generate jobs, so that they can be connected
        project = new XmlParser().parse(new StringReader(configXml))
    }

    def configure(Closure configureClosure) {
        configureClosure.delegate = new ConfigureDelegate(project) // TODO we might need a layer between the Node and the Closure
        configureClosure.call()
    }

    public static class ConfigureDelegate {
        Node project

        ConfigureDelegate(Node project) {
            this.project = project
        }

        def methodMissing(String name, args) { 
            // look on project
            println "1: missing method $name"
        }
    }

    def getXml() {
        String configStr = XmlUtil.serialize(job.project)
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