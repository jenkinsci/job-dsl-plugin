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

    void bundledIcon(String name) {
        withXmlActions << WithXmlAction.create { Node folder ->
            folder / icon(class:'com.cloudbees.hudson.plugins.folder.icons.BuiltinFolderIcon') {
                baseName(name)
            }
        }
    }

    void iconUrl(String iconUrl) {
        withXmlActions << WithXmlAction.create { Node folder ->
            folder / icon(class:'com.cloudbees.hudson.plugins.folder.icons.UrlFolderIcon') {
                url(iconUrl) 
            }
        }
    }

    Node getNode() {
        Node root = new XmlParser().parse(new StringReader(TEMPLATE))
        withXmlActions.each { it.execute(root) }
        root
    }

    protected void execute(Closure rootClosure) {
        withXmlActions << new WithXmlAction(rootClosure)
    }

    private static final String TEMPLATE = '''<?xml version='1.0' encoding='UTF-8'?>
<com.cloudbees.hudson.plugins.folder.Folder>
    <actions/>
    <properties/>
    <icon class="com.cloudbees.hudson.plugins.folder.icons.StockFolderIcon"/>
    <views>
        <hudson.model.AllView>
            <owner class="com.cloudbees.hudson.plugins.folder.Folder" reference="../../.."/>
            <name>All</name>
            <filterExecutors>false</filterExecutors>
            <filterQueue>false</filterQueue>
            <properties class="hudson.model.View$PropertyList"/>
        </hudson.model.AllView>
    </views>
    <viewsTabBar class="hudson.views.DefaultViewsTabBar"/>
    <primaryView>All</primaryView>
    <healthMetrics>
        <com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric/>
    </healthMetrics>
</com.cloudbees.hudson.plugins.folder.Folder>'''
}
