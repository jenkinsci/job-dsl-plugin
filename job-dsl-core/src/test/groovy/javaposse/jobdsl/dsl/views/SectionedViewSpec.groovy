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

    def 'minimal job graphs section'() {
        when:
        view.sections {
            jobGraphs {
                name('test')
            }
        }

        then:
        compareXML(minimalJobGraphsSectionXml, view.xml).similar()
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

    def 'minimal test result section'() {
        when:
        view.sections {
            testResult {
                name('test')
            }
        }

        then:
        compareXML(minimalTestResultSectionXml, view.xml).similar()
    }

    def 'minimal text section'() {
        when:
        view.sections {
            text {
                name('test')
            }
        }

        then:
        compareXML(minimalTextSectionXml, view.xml).similar()
    }

    def 'minimal view list section'() {
        when:
        view.sections {
            viewListing {
                name('test')
            }
        }

        then:
        compareXML(minimalViewListSectionXml, view.xml).similar()
    }

    def 'view list section'() {
        when:
        view.sections {
            viewListing {
                name('test')
                view('view-b')
                view('view-a')
                columns(2)
            }
        }

        then:
        compareXML(viewListSectionXml, view.xml).similar()
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <sections/>
</hudson.plugins.sectioned__view.SectionedView>'''

    def minimalJobGraphsSectionXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.JobGraphsSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <width>FULL</width>
            <alignment>CENTER</alignment>
        </hudson.plugins.sectioned__view.JobGraphsSection>
    </sections>
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

    def minimalTestResultSectionXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.TestResultViewSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <width>FULL</width>
            <alignment>CENTER</alignment>
        </hudson.plugins.sectioned__view.TestResultViewSection>
    </sections>
</hudson.plugins.sectioned__view.SectionedView>'''

    def minimalTextSectionXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.TextSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <width>FULL</width>
            <alignment>CENTER</alignment>
            <text></text>
            <style>NONE</style>
        </hudson.plugins.sectioned__view.TextSection>
    </sections>
</hudson.plugins.sectioned__view.SectionedView>'''

    def minimalViewListSectionXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.ViewListingSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <width>FULL</width>
            <alignment>CENTER</alignment>
            <views></views>
            <columns>1</columns>
        </hudson.plugins.sectioned__view.ViewListingSection>
    </sections>
</hudson.plugins.sectioned__view.SectionedView>'''

    def viewListSectionXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.sectioned__view.SectionedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <sections>
        <hudson.plugins.sectioned__view.ViewListingSection>
            <jobNames>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <name>test</name>
            <width>FULL</width>
            <alignment>CENTER</alignment>
            <views>
                <string>view-a</string>
                <string>view-b</string>
            </views>
            <columns>2</columns>
        </hudson.plugins.sectioned__view.ViewListingSection>
    </sections>
</hudson.plugins.sectioned__view.SectionedView>'''
}
