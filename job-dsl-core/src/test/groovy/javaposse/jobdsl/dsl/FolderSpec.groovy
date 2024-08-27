package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class FolderSpec extends Specification {
    private static final String XML = '''<?xml version='1.0' encoding='UTF-8'?>
<com.cloudbees.hudson.plugins.folder.Folder>
    <actions/>
    <description>la la la</description>
    <displayName>Test Folder</displayName>
    <properties/>
    <icon class="com.cloudbees.hudson.plugins.folder.icons.StockFolderIcon"/>
    <folderViews class="com.cloudbees.hudson.plugins.folder.views.DefaultFolderViewHolder">
        <views>
            <hudson.model.AllView>
                <owner class="com.cloudbees.hudson.plugins.folder.Folder" reference="../../../.."/>
                <name>all</name>
                <filterExecutors>false</filterExecutors>
                <filterQueue>false</filterQueue>
                <properties class="hudson.model.View$PropertyList"/>
            </hudson.model.AllView>
        </views>
        <tabBar class="hudson.views.DefaultViewsTabBar"/>
        <primaryView>Some View</primaryView>
    </folderViews>
    <healthMetrics>
        <com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric/>
    </healthMetrics>
</com.cloudbees.hudson.plugins.folder.Folder>'''

    private final JobManagement jobManagement = Mock(JobManagement)
    private final Folder folder = new Folder(jobManagement, 'test')

    def setupSpec() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'displayName'() {
        when:
        folder.displayName('foo')

        then:
        Node root = folder.node
        root.displayName.size() == 1
        root.displayName[0].text() == 'foo'
    }

    def 'description'() {
        when:
        folder.description('test folder')

        then:
        Node root = folder.node
        root.description.size() == 1
        root.description[0].text() == 'test folder'
    }

    def 'primaryView'() {
        when:
        folder.primaryView('test primaryView')

        then:
        Node root = folder.node
        root.folderViews.size() == 1
        root.folderViews[0].primaryView.size() == 1
        root.folderViews[0].primaryView[0].text() == 'test primaryView'
    }

    def 'views'() {
        when:
        folder.views {
            listView('test')
        }

        then:
        Node root = folder.node
        root.folderViews.size() == 1
        root.folderViews[0].views.size() == 1
        root.folderViews[0].views[0].'hudson.model.ListView'.size() == 1
        root.folderViews[0].views[0].'hudson.model.ListView'[0].children().size() == 8
        root.folderViews[0].views[0].'hudson.model.ListView'[0].name[0].text() == 'test'
        root.folderViews[0].views[0].'hudson.model.ListView'[0].owner[0].attribute('reference') == '../../../..'
        root.folderViews[0].views[0].'hudson.model.ListView'[0].owner[0].attribute('class') ==
                'com.cloudbees.hudson.plugins.folder.Folder'
    }

    def 'call authorization'() {
        setup:
        String propertyName = 'com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty'
        jobManagement.getPermissions(propertyName) >> [
                'hudson.model.Item.Build',
                'hudson.model.Item.Configure',
        ]

        when:
        folder.authorization {
            permission('hudson.model.Item.Build:jill')
            permission('USER:hudson.model.Item.Build:andi')
            permission('GROUP:hudson.model.Item.Build:admin')
            permission('hudson.model.Item.Configure', 'jack')
            userPermission('hudson.model.Item.Configure', 'foo')
            groupPermission('hudson.model.Item.Configure', 'bar')
            permissionAll('anonymous')
            userPermissionAll('alex')
            groupPermissionAll('devs')
            permissions('janice', [
                'hudson.model.Item.Build',
                'hudson.model.Item.Configure'
            ])
            userPermissions('pedro', [
                'hudson.model.Item.Build',
                'hudson.model.Item.Configure'
            ])
            groupPermissions('tanya', [
                'hudson.model.Item.Build',
                'hudson.model.Item.Configure'
            ])
        }

        then:
        NodeList permissions = folder.node.properties[0]."$propertyName"[0].permission
        permissions.size() == 18
        int index = -1
        permissions[++index].text() == 'hudson.model.Item.Build:jill'
        permissions[++index].text() == 'USER:hudson.model.Item.Build:andi'
        permissions[++index].text() == 'GROUP:hudson.model.Item.Build:admin'
        permissions[++index].text() == 'hudson.model.Item.Configure:jack'
        permissions[++index].text() == 'USER:hudson.model.Item.Configure:foo'
        permissions[++index].text() == 'GROUP:hudson.model.Item.Configure:bar'
        permissions[++index].text() == 'hudson.model.Item.Build:anonymous'
        permissions[++index].text() == 'hudson.model.Item.Configure:anonymous'
        permissions[++index].text() == 'USER:hudson.model.Item.Build:alex'
        permissions[++index].text() == 'USER:hudson.model.Item.Configure:alex'
        permissions[++index].text() == 'GROUP:hudson.model.Item.Build:devs'
        permissions[++index].text() == 'GROUP:hudson.model.Item.Configure:devs'
        permissions[++index].text() == 'hudson.model.Item.Build:janice'
        permissions[++index].text() == 'hudson.model.Item.Configure:janice'
        permissions[++index].text() == 'USER:hudson.model.Item.Build:pedro'
        permissions[++index].text() == 'USER:hudson.model.Item.Configure:pedro'
        permissions[++index].text() == 'GROUP:hudson.model.Item.Build:tanya'
        permissions[++index].text() == 'GROUP:hudson.model.Item.Configure:tanya'
    }

    def 'call properties'() {
        when:
        folder.properties {
            propertiesNodes << new Node(null, 'hack')
        }

        then:
        folder.node.properties[0].children()[0].name() == 'hack'
    }

    def 'call icon'() {
        when:
        folder.icon {
            icon = new Node(null,
                    'icon', ['class': 'jenkins.plugins.foldericon.CustomFolderIcon', 'plugin': 'custom-folder-icon'],
                    new Node(null,
                            'customFolderIcon', 'test.png'))
        }

        then:
        folder.node.icon[0].name() == 'icon'
        folder.node.icon[0].attribute('class') == 'jenkins.plugins.foldericon.CustomFolderIcon'
        folder.node.icon[0].attribute('plugin') == 'custom-folder-icon'
        folder.node.icon[0].children()[0].name() == 'customFolderIcon'
        folder.node.icon[0].children()[0].value() == 'test.png'
    }

    def 'configure'() {
        when:
        folder.configure {
            it / foo('bar')
        }

        then:
        Node root = folder.node
        root.foo.size() == 1
        root.foo[0].text() == 'bar'
    }

    def 'xml'() {
        setup:
        folder.displayName('Test Folder')
        folder.description('la la la')
        folder.primaryView('Some View')

        when:
        String xml = folder.xml

        then:
        compareXML(XML, xml).similar()
    }
}
