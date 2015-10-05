package javaposse.jobdsl.dsl

abstract class Item extends AbstractContext {
    String name

    List<WithXmlAction> withXmlActions = []

    protected Item(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Allows direct manipulation of the generated XML.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure withXmlClosure) {
        withXmlActions.add(new WithXmlAction(withXmlClosure))
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

    abstract Node getNode()
}
