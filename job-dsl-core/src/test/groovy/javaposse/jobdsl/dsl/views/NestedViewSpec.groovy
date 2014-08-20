package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.ViewType
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class NestedViewSpec extends Specification {
    NestedView view = new NestedView()

    def setup() {
        setIgnoreWhitespace(true)
    }

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        compareXML(defaultXml, xml).similar()
    }

    def 'nested view columns'() {
        when:
        view.columns {
            status()
            weather()
        }

        then:
        compareXML(nestedViewColumnsXml, view.xml).similar()
    }

    def 'nested view views'() {
        when:
        view.views {
            delegate.view {
                name('foo')
            }
            delegate.view(type: ViewType.SectionedView) {
                name('bar')
            }
        }

        then:
        compareXML(nestedViewViewsXml, view.xml).similar()
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.nested__view.NestedView>
    <name>nested</name>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <views></views>
</hudson.plugins.nested__view.NestedView>'''

    def nestedViewColumnsXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.nested__view.NestedView>
    <name>nested</name>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <views></views>
    <columns>
        <columns>
            <hudson.views.StatusColumn></hudson.views.StatusColumn>
            <hudson.views.WeatherColumn></hudson.views.WeatherColumn>
        </columns>
    </columns>
</hudson.plugins.nested__view.NestedView>'''

    def nestedViewViewsXml = '''<hudson.plugins.nested__view.NestedView>
    <name>nested</name>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <views>
        <hudson.model.ListView>
            <filterExecutors>false</filterExecutors>
            <filterQueue>false</filterQueue>
            <properties class='hudson.model.View$PropertyList'></properties>
            <jobNames class='tree-set'>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <columns></columns>
            <name>foo</name>
            <owner class='hudson.plugins.nested_view.NestedView' reference='../../..'></owner>
        </hudson.model.ListView>
        <hudson.plugins.sectioned__view.SectionedView>
            <filterExecutors>false</filterExecutors>
            <filterQueue>false</filterQueue>
            <properties class='hudson.model.View$PropertyList'></properties>
            <sections></sections>
            <name>bar</name>
            <owner class='hudson.plugins.nested_view.NestedView' reference='../../..'></owner>
        </hudson.plugins.sectioned__view.SectionedView>
    </views>
</hudson.plugins.nested__view.NestedView>'''
}
