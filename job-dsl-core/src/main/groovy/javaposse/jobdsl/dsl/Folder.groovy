package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.NestedViewsContext

/**
 * DSL element representing a Jenkins folder.
 */
class Folder extends AbstractFolder {
    Folder(JobManagement jobManagement, String name) {
        super(jobManagement, name)
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
        new XmlParser().parse(this.class.getResourceAsStream("${this.class.simpleName}-template.xml"))
    }
}
