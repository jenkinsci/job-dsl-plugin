package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class FolderSpec extends Specification {
    Folder folder = new Folder()

    def setupSpec() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'name'() {
        when:
        folder.name('test')

        then:
        folder.name == 'test'
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

        when:
        String xml = folder.xml

        then:
        compareXML(XML, xml).similar()
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
    <primaryView>All</primaryView>
    <healthMetrics>
        <com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric/>
    </healthMetrics>
</com.cloudbees.hudson.plugins.folder.Folder>'''
}
