package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.ScmContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext

abstract class Item extends AbstractContext {
    String name

    private final List<Closure> configureBlocks = []

    protected Item(JobManagement jobManagement, String name) {
        super(jobManagement)
        this.name = name
    }

    @Deprecated
    protected Item(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Deprecated
    void setName(String name) {
        this.name = name
}

    /**
     * Allows direct manipulation of the generated XML.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        // verify that no restrictions are violated before we add
        if (jobManagement.restrictRawJobDsl()) {
            WhitelistHelper.verifyRawJobDsl(configureBlock, jobManagement.getAllowedRawJobdslElementsAsNode())
        }
        if (jobManagement.restrictExternalClassesThatDefineJobDslBlocks()) {
            WhitelistHelper.verifyExternalClassThatDefinesConfigureBlock(configureBlock,
                    jobManagement.getAllowedExternalClassesThatDefineJobDslBlocks())
        }
        configureBlocks << configureBlock
    }

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     */
    String getXml() {
        Writer xmlOutput = new StringWriter()
        XmlNodePrinter xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), '    ')
        xmlNodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            quote = "'" // Use single quote for attributes
        }
        xmlNodePrinter.print(node)

        xmlOutput.toString()
    }

    Map getProperties() {
        // see JENKINS-22708
        throw new UnsupportedOperationException()
    }

    Node getNode() {
        Node node = nodeTemplate
        ContextHelper.executeConfigureBlocks(node, configureBlocks)
        node
    }

    protected Node getNodeTemplate() {
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))
    }

    @Deprecated
    void executeWithXmlActions(Node root) {
        ContextHelper.executeConfigureBlocks(root, configureBlocks)
    }
}
