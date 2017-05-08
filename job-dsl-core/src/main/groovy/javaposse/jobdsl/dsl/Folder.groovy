package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.AuthorizationContext
import javaposse.jobdsl.dsl.helpers.properties.FolderPropertiesContext

/**
 * DSL element representing a Jenkins folder.
 */
class Folder extends Item {
    private static final AUTHORIZATION_MATRIX_PROPERTY_NAME =
            'com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty'

    Folder(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Sets the name to display instead of the actual folder name.
     */
    void displayName(String displayName) {
        configure {
            it / methodMissing('displayName', displayName)
        }
    }

    /**
     * Sets a description for the folder.
     */
    void description(String description) {
        configure {
            it / methodMissing('description', description)
        }
    }

    /**
     * Changes the initial view to show when the folder contains multiple views. Defaults to the {@code 'All'} view.
     *
     * @since 1.36
     */
    void primaryView(String primaryViewArg) {
        configure {
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

        configure { Node project ->
            Node authorizationMatrixProperty = project / 'properties' / AUTHORIZATION_MATRIX_PROPERTY_NAME
            context.permissions.each { String perm ->
                authorizationMatrixProperty.appendNode('permission', perm)
            }
        }
    }

    /**
     * Adds custom properties to the folder.
     *
     * @since 1.47
     */
    void properties(@DslContext(FolderPropertiesContext) Closure closure) {
        FolderPropertiesContext context = new FolderPropertiesContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        configure { Node project ->
            context.propertiesNodes.each {
                project / 'properties' << it
            }
        }
    }
}
