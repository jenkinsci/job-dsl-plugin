package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewType
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class NestedViewSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    NestedView view = new NestedView(jobManagement)

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

    def 'nested view with deprecated view method'() {
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
        4 * jobManagement.logDeprecationWarning()
    }

    def 'nested view with other deprecated view method'() {
        when:
        view.views {
            delegate.view('foo') {
            }
            delegate.view('bar', type: ViewType.SectionedView) {
            }
        }

        then:
        compareXML(nestedViewViewsXml, view.xml).similar()
        2 * jobManagement.logDeprecationWarning()
    }

    def 'nested view with views'() {
        when:
        view.views {
            listView('foo')
            sectionedView('bar')
        }

        then:
        compareXML(nestedViewViewsXml, view.xml).similar()
    }

    def 'nested list view'() {
        when:
        View nestedView
        view.views {
            nestedView = listView('test') {
                description('foo')
            }
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof ListView
        view.node.views[0].children()[0].name() == 'hudson.model.ListView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'nested list view without closure'() {
        when:
        View nestedView
        view.views {
            nestedView = listView('test')
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof ListView
        view.node.views[0].children()[0].name() == 'hudson.model.ListView'
    }

    def 'nested build pipeline view'() {
        when:
        View nestedView
        view.views {
            nestedView = buildPipelineView('test') {
                description('foo')
            }
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof BuildPipelineView
        view.node.views[0].children()[0].name() == 'au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'nested build pipeline view without closure'() {
        when:
        View nestedView
        view.views {
            nestedView = buildPipelineView('test')
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof BuildPipelineView
        view.node.views[0].children()[0].name() == 'au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView'
    }

    def 'nested build monitor view'() {
        when:
        View nestedView
        view.views {
            nestedView = buildMonitorView('test') {
                description('foo')
            }
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof BuildMonitorView
        view.node.views[0].children()[0].name() == 'com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'nested build monitor view without closure'() {
        when:
        View nestedView
        view.views {
            nestedView = buildMonitorView('test')
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof BuildMonitorView
        view.node.views[0].children()[0].name() == 'com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView'
    }

    def 'nested sectioned view'() {
        when:
        View nestedView
        view.views {
            nestedView = sectionedView('test') {
                description('foo')
            }
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof SectionedView
        view.node.views[0].children()[0].name() == 'hudson.plugins.sectioned__view.SectionedView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'nested sectioned view without closure'() {
        when:
        View nestedView
        view.views {
            nestedView = sectionedView('test')
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof SectionedView
        view.node.views[0].children()[0].name() == 'hudson.plugins.sectioned__view.SectionedView'
    }

    def 'nested nested view'() {
        when:
        View nestedView
        view.views {
            nestedView = delegate.nestedView('test') {
                description('foo')
            }
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof NestedView
        view.node.views[0].children()[0].name() == 'hudson.plugins.nested__view.NestedView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'nested nested view without closure'() {
        when:
        View nestedView
        view.views {
            nestedView = delegate.nestedView('test')
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof NestedView
        view.node.views[0].children()[0].name() == 'hudson.plugins.nested__view.NestedView'
    }

    def 'should add categorized jobs view'() {
        when:
        View categorizedView
        view.views {
            categorizedView = delegate.categorizedJobsView('test') {
                description('foo')
            }
        }

        then:
        categorizedView.name == 'test'
        categorizedView instanceof CategorizedJobsView
        view.node.views[0].children()[0].name() == 'org.jenkinsci.plugins.categorizedview.CategorizedJobsView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'should add categorized jobs view without closure'() {
        when:
        View categorizedView
        view.views {
            categorizedView = delegate.categorizedJobsView('test')
        }

        then:
        categorizedView.name == 'test'
        categorizedView instanceof CategorizedJobsView
        view.node.views[0].children()[0].name() == 'org.jenkinsci.plugins.categorizedview.CategorizedJobsView'
    }

    def 'nested delivery pipeline view'() {
        when:
        View nestedView
        view.views {
            nestedView = delegate.deliveryPipelineView('test') {
                description('foo')
            }
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof DeliveryPipelineView
        view.node.views[0].children()[0].name() == 'se.diabol.jenkins.pipeline.DeliveryPipelineView'
        view.node.views[0].children()[0].description[0].text() == 'foo'
    }

    def 'nested delivery pipeline view without closure'() {
        when:
        View nestedView
        view.views {
            nestedView = delegate.deliveryPipelineView('test')
        }

        then:
        nestedView.name == 'test'
        nestedView instanceof DeliveryPipelineView
        view.node.views[0].children()[0].name() == 'se.diabol.jenkins.pipeline.DeliveryPipelineView'
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.nested__view.NestedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <views></views>
</hudson.plugins.nested__view.NestedView>'''

    def nestedViewColumnsXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.nested__view.NestedView>
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
