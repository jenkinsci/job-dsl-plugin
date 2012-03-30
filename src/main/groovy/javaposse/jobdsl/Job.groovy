package javaposse.jobdsl

import java.io.StringReader

class Job {
    Node project = null
    JobManagement jobManagement = null

    Job(JobManagement jobManagement) {
        this.jobManagement = jobManagement;
    }
    
    def using(String templateName) {
        String configXml = jobManagement.getConfig(templateName)
        project = new XmlParser().parse(new StringReader(configXml))
    }

    def configure(Closure configureClosure) {
        configureClosure.delegate = project
        configureClosure.call()
    }
}