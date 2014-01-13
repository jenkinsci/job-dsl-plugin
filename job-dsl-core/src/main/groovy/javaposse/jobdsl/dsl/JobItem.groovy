package javaposse.jobdsl.dsl

import groovy.lang.Closure;
import groovy.lang.Delegate;
import groovy.util.Node;

import java.util.List;

import javaposse.jobdsl.dsl.helpers.ItemHelper;

abstract class JobItem {
    private static int nameChangeTag = 0;

    JobManagement jobManagement
    private JobItem parent

    String name // Required
    String templateName = null // Optional
    private int fullNameTag;
    private String fullName;

    List<WithXmlAction> withXmlActions = []

    @Delegate ItemHelper helperItem

    public JobItem(JobManagement jobManagement, JobItem parent) {
        this.jobManagement = jobManagement;
        this.parent = parent;
        this.fullName = null;
        this.fullNameTag = nameChangeTag - 1;

        helperItem = new ItemHelper(withXmlActions)
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
        nameChangeTag++
    }

    def name(Closure nameClosure) {
        // TODO do we need a delegate?
        name(nameClosure.call().toString())
    }

    String getFullName() {
        if (parent) {
            if (fullName == null || fullNameTag != nameChangeTag) {
                fullName = getFullNameBuilder().toString()
                fullNameTag = nameChangeTag
            }
            return fullName
        } else {
            return name
        }
    }

    protected StringBuilder getFullNameBuilder() {
        if (parent) {
            StringBuilder fullNameBuilder = parent.getFullNameBuilder()
            fullNameBuilder.append('/').append(name)
            return fullNameBuilder
        } else {
            return new StringBuilder(name)
        }
    }

    public Node getNode() {
        Node project = templateName==null?executeEmptyTemplate():executeUsing()

        // TODO check name field

        executeWithXmlActions(project)

        return project
    }

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     * @return
     */
    public String getXml() {
        Node project = getNode()

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

        Node templateNode = new XmlParser().parse(new StringReader(configXml))

        matchTemplate(templateNode);

        // Clean up our own indication that a job is a template
        List<Node> seedJobProperties = templateNode.depthFirst().findAll { it.name() == 'javaposse.jobdsl.plugin.SeedJobsProperty' }
        seedJobProperties.each { Node node -> node.parent().remove(node) }

        return templateNode
    }

    private executeEmptyTemplate() {
        return new XmlParser().parse(new StringReader(getTemplate()))
    }

    protected abstract void matchTemplate(Node templateNode);

    protected abstract String getTemplate();

}
