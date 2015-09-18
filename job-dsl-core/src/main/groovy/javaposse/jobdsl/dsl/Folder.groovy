package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.AuthorizationContext

/**
 * DSL element representing a Jenkins folder.
 */
class Folder extends Item {
    private static final AUTHORIZATION_MATRIX_PROPERTY_NAME =
            'com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty'

    Folder(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the name to display instead of the actual folder name.
     */
    void displayName(String displayName) {
        execute {
            it / methodMissing('displayName', displayName)
        }
    }

    /**
     * Sets a description for the folder.
     */
    void description(String description) {
        execute {
            it / methodMissing('description', description)
        }
    }

    /**
     * Changes the initial view to show when the folder contains multiple views. Defaults to the {@code 'All'} view.
     *
     * @since 1.36
     */
    void primaryView(String primaryViewArg) {
        execute {
            it / methodMissing('primaryView', primaryViewArg)
        }
    }

    /**
     * Creates permission records.
     *
     * @since 1.31
     */
    void authorization(@DslContext(AuthorizationContext) Closure closure) {
        AuthorizationContext context = new AuthorizationContext(jobManagement, AUTHORIZATION_MATRIX_PROPERTY_NAME)
        ContextHelper.executeInContext(closure, context)

        withXmlActions << WithXmlAction.create { Node project ->
            Node authorizationMatrixProperty = project / 'properties' / AUTHORIZATION_MATRIX_PROPERTY_NAME
            context.permissions.each { String perm ->
                authorizationMatrixProperty.appendNode('permission', perm)
            }
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
