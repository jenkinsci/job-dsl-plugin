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
            if (nodeAlreadyPresent(name)) {
                project.get(name)[0].value = args[0]
            } else {
                project.appendNode(name, args[0])
            }
        }

        private boolean nodeAlreadyPresent(String nodeName) {
            return project.get(nodeName).size() > 0
        }
    }

    public String getXml() {
        //new XmlNodePrinter(new PrintWriter(new FileWriter(new File('job.xml')))).print(project)

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