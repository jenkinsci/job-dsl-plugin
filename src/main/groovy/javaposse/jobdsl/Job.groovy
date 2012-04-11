package javaposse.jobdsl

import groovy.util.Node;
import groovy.xml.XmlUtil
import java.io.StringReader

/**
 * DSL Element representing a Jenkins Job
 */
public class Job {
    String name // Required
    Node project
    JobManagement jobManagement

    public Job(JobManagement jobManagement) {
        super();
        this.jobManagement = jobManagement;
    }

    /**
     * Creates a new job configuration, based on the job template referenced by the parameter
     * @param templateName the name of the template upon which to base the new job
     * @return a new graph of groovy.util.Node, representing the job configuration structure
     */
    def using(String templateName) {
        String configXml = jobManagement.getConfig(templateName)
        // TODO record which templates are used to generate jobs, so that they can be connected
        project = new XmlParser().parse(new StringReader(configXml))
    }

    def configure(Closure configureClosure) {
        configureClosure.delegate = new NodeDelegate(project)
        configureClosure.call(project) // make Xml Node available, in case it needs to be passed to other methods
    }

    public static class NodeDelegate {
        Node node

        NodeDelegate(Node node) {
            this.node = node
        }

        def methodMissing(String name, args) {
            if (args.length == 0) {
                return // Not sure what to do with a method with no args in this context
            }

            // Identify Node
            def targetNode
            if (nodeAlreadyPresent(name)) {
                targetNode = project.get(name)[0]
            } else {
                targetNode = project.appendNode(name)
            }

            if (args[0] instanceof Closure) {
                // block that wants to be configured
                def childClosure = args[0]
                childClosure.delegate = new NodeDelegate(targetNode)
                childClosure.call(targetNode)
            } else {
                // Default to setting direct value
                targetNode.value = args[0]
            }

        }

        private boolean nodeAlreadyPresent(String nodeName) {
            return node.get(nodeName).size() > 0
        }
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