package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.base.Strings

abstract class Item extends AbstractContext {
    String name

    List<WithXmlAction> withXmlActions = []

    protected Item(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Deprecated
    void name(String name) {
        jobManagement.logDeprecationWarning()
        if (this.name) {
            Preconditions.checkState(Strings.isNullOrEmpty(name), 'name can only be set once')
        }
        this.name = name
    }

    /**
     * Provide raw config.xml for direct manipulation.
     */
    void configure(Closure withXmlClosure) {
        withXmlActions.add( new WithXmlAction(withXmlClosure) )
    }

    void with(Closure closure) {
        boolean sessionStarted = false
        if (name) {
            jobManagement.startSession(name)
            sessionStarted = true
        }
        try {
            super.with(closure)
        } finally {
            if (sessionStarted) {
                jobManagement.stopSession()
            }
        }
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
