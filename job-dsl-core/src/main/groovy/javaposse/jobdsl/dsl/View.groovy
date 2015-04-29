package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.base.Strings

/**
 * DSL element representing a Jenkins view.
 */
abstract class View extends AbstractContext {
    private final List<WithXmlAction> withXmlActions = []

    String name

    protected View(JobManagement jobManagement) {
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

    void description(String descriptionArg) {
        execute {
            it / methodMissing('description', descriptionArg)
        }
    }

    void filterBuildQueue(boolean filterBuildQueueArg = true) {
        execute {
            it / methodMissing('filterQueue', filterBuildQueueArg)
        }
    }

    void filterExecutors(boolean filterExecutorsArg = true) {
        execute {
            it / methodMissing('filterExecutors', filterExecutorsArg)
        }
    }

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

    Node getNode() {
        Node root = new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))

        withXmlActions.each { it.execute(root) }
        root
    }

    protected void execute(Closure rootClosure) {
        withXmlActions << new WithXmlAction(rootClosure)
    }
}
