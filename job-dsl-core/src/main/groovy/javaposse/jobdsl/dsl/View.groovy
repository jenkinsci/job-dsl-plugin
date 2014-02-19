package javaposse.jobdsl.dsl


/**
 * DSL element representing a Jenkins view.
 */
public abstract class View extends XmlConfig {

    public View() {
        super(XmlConfigType.VIEW)
    }

    void name(String name) {
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

    Node getNode() {
        Node root = new XmlParser().parse(new StringReader(getTemplate()))
        root
    }

    protected void execute(Closure rootClosure) {
        withXmlActions << new WithXmlAction(rootClosure)
    }

    protected abstract String getTemplate();
}
