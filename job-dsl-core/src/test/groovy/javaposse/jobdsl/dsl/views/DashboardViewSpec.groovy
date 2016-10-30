package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.views.portlets.TestTrendChartContext

class DashboardViewSpec extends ListViewSpec<DashboardView> {
    def setup() {
        view = new DashboardView(jobManagement, 'test')
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

    def 'test build statistics with no options'() {
        when:
        view.topPortlets {
            buildStatistics()
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.stats.StatBuilds'
            children().size() == 2
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'Build statistics'
        }
    }

    def 'test build statistics with all options'() {
        when:
        view.topPortlets {
            buildStatistics {
                displayName('bar')
            }
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.stats.StatBuilds'
            children().size() == 2
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'bar'
        }
    }

    def 'test iframe with no options'() {
        when:
        view.topPortlets {
            iframe()
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.core.IframePortlet'
            children().size() == 5
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'Iframe Portlet'
            iframeSource[0].value() == ''
            effectiveUrl[0].value() == ''
            divStyle[0].value() == 'width:100%;height:1000px;'
        }
    }

    def 'test iframe with all options'() {
        when:
        view.topPortlets {
            iframe {
                displayName('bar')
                iframeSource('one')
                effectiveUrl('three')
                divStyle('four')
            }
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.core.IframePortlet'
            children().size() == 5
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'bar'
            iframeSource[0].value() == 'one'
            effectiveUrl[0].value() == 'three'
            divStyle[0].value() == 'four'
        }
    }

    def 'test jenkins jobs list with no options'() {
        when:
        view.topPortlets {
            jenkinsJobsList()
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.core.HudsonStdJobsPortlet'
            children().size() == 2
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'Jenkins jobs list'
        }
    }

    def 'test jenkins jobs list with all options'() {
        when:
        view.topPortlets {
            jenkinsJobsList {
                displayName('bar')
            }
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.core.HudsonStdJobsPortlet'
            children().size() == 2
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'bar'
        }
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

    def 'test trend chart with no options'() {
        when:
        view.topPortlets {
            testTrendChart()
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.test.TestTrendChart'
            children().size() == 7
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'Test Trend Chart'
            graphWidth[0].value() == 300
            graphHeight[0].value() == 220
            dateRange[0].value() == 0
            dateShift[0].value() == 0
            displayStatus[0].value() == TestTrendChartContext.DisplayStatus.ALL
        }
    }

    def 'test trend chart with all options'() {
        when:
        view.topPortlets {
            testTrendChart {
                displayName('bar')
                graphWidth(100)
                graphHeight(200)
                dateRange(7)
                dateShift(1)
                displayStatus(TestTrendChartContext.DisplayStatus.SUCCESS)
            }
        }

        then:
        with(view.node.topPortlets[0].children()[0]) {
            name() == 'hudson.plugins.view.dashboard.test.TestTrendChart'
            children().size() == 7
            id[0].value() ==~ /dashboard_portlet_\d+/
            name[0].value() == 'bar'
            graphWidth[0].value() == 100
            graphHeight[0].value() == 200
            dateRange[0].value() == 7
            dateShift[0].value() == 1
            displayStatus[0].value() == TestTrendChartContext.DisplayStatus.SUCCESS
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
