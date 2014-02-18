package javaposse.jobdsl.dsl

import groovy.lang.Closure;
import groovy.util.Node;

import java.util.List;

public abstract class XmlConfig {
    
    XmlConfigType configType;
    
    String name;

    List<WithXmlAction> withXmlActions = []
    
    public XmlConfig(XmlConfigType configType) {
        this.configType = configType
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

    /**
     * Postpone all xml processing until someone actually asks for the xml. That lets us execute everything in order,
     * even if the user didn't specify them in order.
     * @return
     */
    public String getXml() {
        Node root = getNode()

        def xmlOutput = new StringWriter()
        def xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), "    ")
        xmlNodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            quote = "'" // Use single quote for attributes
        }
        xmlNodePrinter.print(root)

        String configStr = xmlOutput.toString()
        return configStr
    }

    protected void executeWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        // TODO Some Node magic to copy it at each phase, and then presenting a diff in the logs
        withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    /**
     * The root node of the configuration. In XML.
     * @return Node
     */
    protected abstract Node getNode();
    
}
