package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.additional.AdditionalXmlConfig

abstract class Item implements Context {
    String name

    List<WithXmlAction> withXmlActions = []

    ItemType configType

    protected Item(ItemType configType) {
        this.configType = configType
    }

    List<AdditionalXmlConfig> additionalConfigs = []

    String templateName = null // Optional

    void name(String name) {
        this.name = name
    }

    /**
     * Provide raw config.xml for direct manipulation.
     */
    void configure(Closure withXmlClosure) {
        withXmlActions.add( new WithXmlAction(withXmlClosure) )
    }

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     * @return
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

    protected void executeWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        // TODO Some Node magic to copy it at each phase, and then presenting a diff in the logs
        withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    /**
     * The root node of the template. Empty content.
     * @return Node
     */
    protected abstract Node getRootNode()

    @SuppressWarnings('UnnecessaryGetter')
    Node getNode() {
        Node root = getRootNode()
        executeWithXmlActions(root)
        root
    }
}
