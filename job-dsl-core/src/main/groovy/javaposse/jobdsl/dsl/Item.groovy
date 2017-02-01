package javaposse.jobdsl.dsl

abstract class Item extends AbstractContext {
    String name
    boolean isRestrictedRawJobDsl
    boolean isRestrictedExternalJobDsl

    private final List<Closure> configureBlocks = []

    protected Item(JobManagement jobManagement, String name) {
        super(jobManagement)
        this.name = name
        this.isRestrictedExternalJobDsl = (jobManagement != null) ? jobManagement.isRestrictedExternalJobDsl() : false
        this.isRestrictedRawJobDsl = (jobManagement != null) ? jobManagement.isRestrictedRawJobDsl() : false
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
        // verify that no restrictions are violated before we add the configure blocks to be processed
        if (this.isRestrictedRawJobDsl) {
            WhitelistHelper.verifyRawJobDsl(configureBlock, jobManagement.allowedRawJobdslElementsAsNode, null)
        }
        if (this.isRestrictedExternalJobDsl) {
            WhitelistHelper.verifyExternalClassThatDefinesConfigureBlock(configureBlock,
                    jobManagement.allowedExternalClassesThatDefineJobDslBlocks)
        }
        configureBlocks << configureBlock
    }

    /**
     * Allows direct manipulation of the generated XML.
     * This configure method focus' on configure call that stem from the Job class, where the
     * original config block must be passed in so we can evaluate where it came from.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock, Closure originalConfigureBlock) {
        // verify that no restrictions are violated before we add the configure blocks to be processed
        if (this.isRestrictedRawJobDsl) {
            WhitelistHelper.verifyRawJobDsl(configureBlock, jobManagement.allowedRawJobdslElementsAsNode,
                    originalConfigureBlock)
        }
        // when this method is called, we are checking the original configure block. This is the object that will
        // have the correct class information that it was inherited from
        if (this.isRestrictedExternalJobDsl) {
            WhitelistHelper.verifyExternalClassThatDefinesConfigureBlock(originalConfigureBlock,
                    jobManagement.allowedExternalClassesThatDefineJobDslBlocks)
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
