package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.jobfilter.Status
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class SectionedViewSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    SectionedView view = new SectionedView(jobManagement, 'test')

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
                jobFilters {
                    status {
                        status(Status.UNSTABLE)
                    }
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
            <jobFilters>
                <hudson.views.JobStatusFilter>
                    <includeExcludeTypeString>includeMatched</includeExcludeTypeString>
                    <unstable>true</unstable>
                    <failed>false</failed>
                    <aborted>false</aborted>
                    <disabled>false</disabled>
                    <stable>false</stable>
                </hudson.views.JobStatusFilter>
            </jobFilters>
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
