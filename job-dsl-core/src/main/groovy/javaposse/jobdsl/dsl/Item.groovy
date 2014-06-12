package javaposse.jobdsl.dsl

abstract class Item {
    String name

    List<WithXmlAction> withXmlActions = []

    void name(String name) {
        this.name = name
    }

    /**
     * Provide raw config.xml for direct manipulation.
     */
    def configure(Closure withXmlClosure) {
        withXmlActions.add( new WithXmlAction(withXmlClosure) )
    }

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     * @return
     */
    String getXml() {
        Writer xmlOutput = new StringWriter()
        XmlNodePrinter xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), "    ")
        xmlNodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            quote = "'" // Use single quote for attributes
        }
        xmlNodePrinter.print(node)

        return xmlOutput.toString()
    }

    abstract Node getNode()
}
