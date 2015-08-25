package javaposse.jobdsl.dsl

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
        this.name = name
    }

    /**
     * Sets a description for the view.
     */
    void description(String description) {
        execute {
            it / methodMissing('description', description)
        }
    }

    /**
     * If set to {@code true}. only jobs in this view will be shown in the build queue. Defaults to {@code false}.
     */
    void filterBuildQueue(boolean filterBuildQueue = true) {
        execute {
            it / methodMissing('filterQueue', filterBuildQueue)
        }
    }

    /**
     * If set to {@code true}, only those build executors will be shown that could execute the jobs in this view.
     * Defaults to {@code false}.
     */
    void filterExecutors(boolean filterExecutors = true) {
        execute {
            it / methodMissing('filterExecutors', filterExecutors)
        }
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

    Node getNode() {
        Node root = new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))

        withXmlActions.each { it.execute(root) }
        root
    }

    protected void execute(Closure rootClosure) {
        withXmlActions << new WithXmlAction(rootClosure)
    }
}
