package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLUnit.compareXML

class NestedViewSpec extends Specification {
    private static final String DEFAULT_XML = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.nested__view.NestedView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class='hudson.model.View$PropertyList'></properties>
    <views></views>
</hudson.plugins.nested__view.NestedView>'''

    private static final String NESTED_VIEW_COLUMNS_XML = '''<?xml version='1.0' encoding='UTF-8'?>
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

    private static final String NESTED_VIEW_VIEWS_XML = '''<hudson.plugins.nested__view.NestedView>
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

    private final JobManagement jobManagement = Mock(JobManagement)
    private final NestedView view = new NestedView(jobManagement, 'test')

    def setup() {
        XMLUnit.ignoreWhitespace = true
    }

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        compareXML(DEFAULT_XML, xml).similar()
    }

    def 'nested view columns'() {
        when:
        view.columns {
            status()
            weather()
        }

        then:
        compareXML(NESTED_VIEW_COLUMNS_XML, view.xml).similar()
    }

    def 'nested view with views'() {
        when:
        view.views {
            listView('foo')
            sectionedView('bar')
        }

        then:
        compareXML(NESTED_VIEW_VIEWS_XML, view.xml).similar()
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
        1 * jobManagement.requirePlugin('build-pipeline-plugin', true)
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
        1 * jobManagement.requirePlugin('build-pipeline-plugin', true)
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
        1 * jobManagement.requirePlugin('build-monitor-plugin', true)
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
        1 * jobManagement.requirePlugin('build-monitor-plugin', true)
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
        1 * jobManagement.requirePlugin('sectioned-view', true)
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
        1 * jobManagement.requirePlugin('sectioned-view', true)
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

    def 'nested categorized jobs view'() {
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
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '1.8', true)
    }

    def 'nested categorized jobs view without closure'() {
        when:
        View categorizedView
        view.views {
            categorizedView = delegate.categorizedJobsView('test')
        }

        then:
        categorizedView.name == 'test'
        categorizedView instanceof CategorizedJobsView
        view.node.views[0].children()[0].name() == 'org.jenkinsci.plugins.categorizedview.CategorizedJobsView'
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '1.8', true)
    }

    def 'nested dashboard view'() {
        when:
        View dashboardView
        view.views {
            dashboardView = delegate.dashboardView('test') {
                description('foo')
            }
        }

        then:
        dashboardView.name == 'test'
        dashboardView instanceof DashboardView
        view.node.views[0].children()[0].name() == 'hudson.plugins.view.dashboard.Dashboard'
        view.node.views[0].children()[0].description[0].text() == 'foo'
        1 * jobManagement.requireMinimumPluginVersion('dashboard-view', '2.9.7', true)
    }

    def 'nested dashboard view without closure'() {
        when:
        View dashboardView
        view.views {
            dashboardView = delegate.dashboardView('test')
        }

        then:
        dashboardView.name == 'test'
        dashboardView instanceof DashboardView
        view.node.views[0].children()[0].name() == 'hudson.plugins.view.dashboard.Dashboard'
        1 * jobManagement.requireMinimumPluginVersion('dashboard-view', '2.9.7', true)
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
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0', true)
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
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0', true)
    }
}
