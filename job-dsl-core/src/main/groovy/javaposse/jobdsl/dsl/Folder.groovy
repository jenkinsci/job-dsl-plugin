package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.helpers.AuthorizationContext
import javaposse.jobdsl.dsl.helpers.properties.FolderPropertiesContext
import javaposse.jobdsl.dsl.views.NestedViewsContext

/**
 * DSL element representing a Jenkins folder.
 */
class Folder extends Item {
    private static final String AUTHORIZATION_MATRIX_PROPERTY_NAME =
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
            Node node = it
            if (jobManagement.isMinimumPluginVersionInstalled('cloudbees-folder', '5.14')) {
                node = node / 'folderViews'
            }
            node / methodMissing('primaryView', primaryViewArg)
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

    /**
     * Adds nested views.
     *
     * @since 1.69
     */
    @RequiresPlugin(id = 'cloudbees-folder', minimumVersion = '5.14')
    void views(@DslContext(NestedViewsContext) Closure viewsClosure) {
        NestedViewsContext context = new NestedViewsContext(jobManagement)
        ContextHelper.executeInContext(viewsClosure, context)

        configure {
            for (View view : context.views) {
                Node viewNode = view.node
                viewNode.appendNode('name', view.name)
                viewNode.appendNode('owner', [
                        class: 'com.cloudbees.hudson.plugins.folder.Folder',
                        reference: '../../../..'
                ])
                it / 'folderViews' / 'views' << viewNode
            }
        }
    }

    @Override
    protected Node getNodeTemplate() {
        String version = jobManagement.isMinimumPluginVersionInstalled('cloudbees-folder', '5.14') ? '-5.14' : ''
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}${version}-template.xml"))
    }
}
