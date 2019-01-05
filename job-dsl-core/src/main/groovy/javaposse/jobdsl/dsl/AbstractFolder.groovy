package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.AuthorizationContext
import javaposse.jobdsl.dsl.helpers.properties.FolderPropertiesContext

abstract class AbstractFolder extends Item {
    private static final String AUTHORIZATION_MATRIX_PROPERTY_NAME =
            'com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty'

    protected AbstractFolder(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Changes the initial view to show when the folder contains multiple views. Defaults to the {@code 'All'} view.
     *
     * @since 1.36
     */
    void primaryView(String primaryViewArg) {
        configure {
            it / 'folderViews' / methodMissing('primaryView', primaryViewArg)
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
