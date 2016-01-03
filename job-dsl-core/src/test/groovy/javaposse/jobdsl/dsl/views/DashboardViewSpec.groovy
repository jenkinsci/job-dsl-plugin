package javaposse.jobdsl.dsl.views

class DashboardViewSpec extends ListViewSpec<DashboardView> {
    def setup() {
        view = new DashboardView(jobManagement)
    }

    def 'portlets added'() {
        when:
        view."$position" {
            testStatisticsChart()
        }

        then:
        Node root = view.node
        root."$position".size() == 1
        root."$position"[0].children().size() == 1
        root."$position"[0].'hudson.plugins.view.dashboard.test.TestStatisticsChart'[0].name[0].value() ==
                'Test Statistics Chart'

        where:
        position << ['topPortlets', 'bottomPortlets', 'leftPortlets', 'rightPortlets']
    }

    def 'test statistics chart with no options'() {
        when:
        view.topPortlets {
            testStatisticsChart()
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.test.TestStatisticsChart'
            children().size() == 2
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'Test Statistics Chart'
        }
    }

    def 'test statistics chart with all options'() {
        when:
        view.topPortlets {
            testStatisticsChart {
                displayName('bar')
            }
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.test.TestStatisticsChart'
            children().size() == 2
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'bar'
        }
    }

    def 'test statistics grid with no options'() {
        when:
        view.topPortlets {
            testStatisticsGrid()
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.test.TestStatisticsPortlet'
            children().size() == 6
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'Test Statistics Grid'
            useBackgroundColors[0].value() == false
            skippedColor[0].value() == 'FDB813'
            successColor[0].value() == '71E66D'
            failureColor[0].value() == 'E86850'
        }
    }

    def 'test statistics grid with all options'() {
        when:
        view.topPortlets {
            testStatisticsGrid {
                displayName('bar')
                skippedColor('one')
                successColor('two')
                failureColor('three')
            }
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.test.TestStatisticsPortlet'
            children().size() == 6
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'bar'
            useBackgroundColors[0].value() == true
            skippedColor[0].value() == 'one'
            successColor[0].value() == 'two'
            failureColor[0].value() == 'three'
        }
    }

    protected String getDefaultXml() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.view.dashboard.Dashboard>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames>
        <comparator class="hudson.util.CaseInsensitiveComparator"/>
    </jobNames>
    <jobFilters/>
    <columns/>
    <recurse>false</recurse>
    <useCssStyle>false</useCssStyle>
    <includeStdJobList>false</includeStdJobList>
    <hideJenkinsPanels>false</hideJenkinsPanels>
    <leftPortletWidth>50%</leftPortletWidth>
    <rightPortletWidth>50%</rightPortletWidth>
    <leftPortlets/>
    <rightPortlets/>
    <topPortlets/>
    <bottomPortlets/>
</hudson.plugins.view.dashboard.Dashboard>'''
    }
}
