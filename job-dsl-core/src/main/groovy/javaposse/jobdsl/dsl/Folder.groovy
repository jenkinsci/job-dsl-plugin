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

    /**
     * @since 1.31
     */
    void authorization(@DslContext(AuthorizationContext) Closure closure) {
        AuthorizationContext context = new AuthorizationContext(jobManagement,  AUTHORIZATION_MATRIX_PROPERTY_NAME)
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
