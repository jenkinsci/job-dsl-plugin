package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class FolderSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    Folder folder = new Folder(jobManagement, 'test')

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
        root.primaryView.size() == 1
        root.primaryView[0].text() == 'test primaryView'
    }

    def 'primaryView with newer plugin version'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('cloudbees-folder', '5.14') >> true

        when:
        folder.primaryView('test primaryView')

        then:
        Node root = folder.node
        root.folderViews.size() == 1
        root.folderViews[0].primaryView.size() == 1
        root.folderViews[0].primaryView[0].text() == 'test primaryView'
    }

    def 'views'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('cloudbees-folder', '5.14') >> true

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
            permission('hudson.model.Item.Configure', 'jack')
            permissionAll('anonymous')
            permissions('janice', [
                'hudson.model.Item.Build',
                'hudson.model.Item.Configure'
            ])
        }

        then:
        NodeList permissions = folder.node.properties[0]."$propertyName"[0].permission
        permissions.size() == 6
        permissions[0].text() == 'hudson.model.Item.Build:jill'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
        permissions[2].text() == 'hudson.model.Item.Build:anonymous'
        permissions[3].text() == 'hudson.model.Item.Configure:anonymous'
        permissions[4].text() == 'hudson.model.Item.Build:janice'
        permissions[5].text() == 'hudson.model.Item.Configure:janice'
    }

    def 'call properties'() {
        when:
        folder.properties {
            propertiesNodes << new Node(null, 'hack')
        }

        then:
        folder.node.properties[0].children()[0].name() == 'hack'
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

    def 'xml for newer plugin version'() {
        setup:
        jobManagement.isMinimumPluginVersionInstalled('cloudbees-folder', '5.14') >> true
        folder.displayName('Test Folder')
        folder.description('la la la')
        folder.primaryView('Some View')

        when:
        String xml = folder.xml

        then:
        compareXML(XML_514, xml).similar()
    }

    private static final String XML = '''<?xml version='1.0' encoding='UTF-8'?>
<com.cloudbees.hudson.plugins.folder.Folder>
    <actions/>
    <description>la la la</description>
    <displayName>Test Folder</displayName>
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
    <primaryView>Some View</primaryView>
    <healthMetrics>
        <com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric/>
    </healthMetrics>
</com.cloudbees.hudson.plugins.folder.Folder>'''

    private static final String XML_514 = '''<?xml version='1.0' encoding='UTF-8'?>
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
                <name>All</name>
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
}
