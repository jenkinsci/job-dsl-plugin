package javaposse.jobdsl.dsl

/**
 * DSL element representing a Jenkins folder.
 */
class Folder extends Item {
    Folder(JobManagement jobManagement) {
        super(jobManagement)
    }

    void displayName(String displayNameArg) {
        execute {
            it / methodMissing('displayName', displayNameArg)
        }
    }

    void description(String descriptionArg) {
        execute {
            it / methodMissing('description', descriptionArg)
        }
    }

    Node getNode() {
        Node root = new XmlParser().parse(this.class.getResourceAsStream('Folder-template.xml'))
        withXmlActions.each { it.execute(root) }
        root
    }

    protected void execute(Closure rootClosure) {
        withXmlActions << new WithXmlAction(rootClosure)
    }
}
