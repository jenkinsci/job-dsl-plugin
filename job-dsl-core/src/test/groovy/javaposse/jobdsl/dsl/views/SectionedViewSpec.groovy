package javaposse.jobdsl.dsl.views

import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class SectionedViewSpec extends Specification {
    SectionedView view = new SectionedView()

    def setup() {
        setIgnoreWhitespace(true)
    }

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        compareXML(defaultXml, xml).similar()
    }

    def 'minimal list view section'() {
        when:
        view.sections {
            listView {
                name('test')
            }
        }

        then:
        compareXML(minimalListViewSectionXml, view.xml).similar()
    }

    def 'complex list view section'() {
        when:
        view.sections {
            listView {
                name('test')
                width('HALF')
                alignment('LEFT')
                jobs {
                    name 'foo'
                    regex 'test-.*'
                }
                columns {
                    status()
                    name()
                }
            }
        }

        then:
        compareXML(complexListViewSectionXml, view.xml).similar()
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <sections/>
</hudson.plugins.sectioned__view.SectionedView>'''

    def minimalListViewSectionXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.ListViewSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <width>FULL</width>
            <alignment>CENTER</alignment>
            <columns></columns>
        </hudson.plugins.sectioned__view.ListViewSection>
    </sections>
</hudson.plugins.sectioned__view.SectionedView>'''

    def complexListViewSectionXml = ''' <hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.ListViewSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
                <string>foo</string>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <includeRegex>test-.*</includeRegex>
            <width>HALF</width>
            <alignment>LEFT</alignment>
            <columns>
                <hudson.views.StatusColumn></hudson.views.StatusColumn>
                <hudson.views.JobColumn></hudson.views.JobColumn>
            </columns>
        </hudson.plugins.sectioned__view.ListViewSection>
    </sections>
</hudson.plugins.sectioned__view.SectionedView>'''
}
