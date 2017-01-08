package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import java.security.SecureRandom

@ContextType('hudson.plugins.view.dashboard.DashboardPortlet')
class DashboardPortletContext extends AbstractExtensibleContext {
    private static final Random RANDOM = new SecureRandom()

    protected final List<Node> portletNodes = []

    DashboardPortletContext(JobManagement jobManagement) {
        super(jobManagement, null)
    }

    @Override
    protected void addExtensionNode(Node node) {
        portletNodes << node
    }

    /**
     * Adds a build statistics portlet.
     *
     * @since 1.46
     */
    void buildStatistics(@DslContext(BuildStatisticsContext) Closure closure = null) {
        BuildStatisticsContext context = new BuildStatisticsContext()
        ContextHelper.executeInContext(closure, context)

        portletNodes << new NodeBuilder().'hudson.plugins.view.dashboard.stats.StatBuilds' {
            id(generatePortletId())
            name(context.displayName ?: '')
        }
    }

    /**
     * Adds an IFrame portlet.
     *
     * @since 1.46
     */
    void iframe(@DslContext(IFrameContext) Closure closure = null) {
        IFrameContext context = new IFrameContext()
        ContextHelper.executeInContext(closure, context)

        portletNodes << new NodeBuilder().'hudson.plugins.view.dashboard.core.IframePortlet' {
            id(generatePortletId())
            name(context.displayName ?: '')
            iframeSource(context.iframeSource ?: '')
            effectiveUrl(context.effectiveUrl ?: '')
            divStyle(context.divStyle ?: '')
        }
    }

    /**
     * Add a jenkins jobs list.
     *
     * @since 1.46
     */
    void jenkinsJobsList(@DslContext(JenkinsJobsListContext) Closure closure = null) {
        JenkinsJobsListContext context = new JenkinsJobsListContext()
        ContextHelper.executeInContext(closure, context)

        portletNodes << new NodeBuilder().'hudson.plugins.view.dashboard.core.HudsonStdJobsPortlet' {
            id(generatePortletId())
            name(context.displayName ?: '')
        }
    }

    /**
     * Adds a test statistics chart.
     */
    void testStatisticsChart(@DslContext(TestStatisticsChartContext) Closure closure = null) {
        TestStatisticsChartContext context = new TestStatisticsChartContext()
        ContextHelper.executeInContext(closure, context)

        portletNodes << new NodeBuilder().'hudson.plugins.view.dashboard.test.TestStatisticsChart' {
            id(generatePortletId())
            name(context.displayName ?: '')
        }
    }

    /**
     * Adds a test statistics grid.
     */
    void testStatisticsGrid(@DslContext(TestStatisticsGridContext) Closure closure = null) {
        TestStatisticsGridContext context = new TestStatisticsGridContext()
        ContextHelper.executeInContext(closure, context)

        portletNodes << new NodeBuilder().'hudson.plugins.view.dashboard.test.TestStatisticsPortlet' {
            id(generatePortletId())
            name(context.displayName ?: '')
            useBackgroundColors(context.useBackgroundColors)
            skippedColor(context.skippedColor ?: '')
            successColor(context.successColor ?: '')
            failureColor(context.failureColor ?: '')
        }
    }

    /**
     * Adds a test trend chart.
     *
     * @since 1.46
     */
    void testTrendChart(@DslContext(TestTrendChartContext) Closure closure = null) {
        TestTrendChartContext context = new TestTrendChartContext()
        ContextHelper.executeInContext(closure, context)

        portletNodes << new NodeBuilder().'hudson.plugins.view.dashboard.test.TestTrendChart' {
            id(generatePortletId())
            name(context.displayName ?: '')
            graphWidth(context.graphWidth)
            graphHeight(context.graphHeight)
            dateRange(context.dateRange)
            dateShift(context.dateShift)
            displayStatus(context.displayStatus)
        }
    }

    private static String generatePortletId() {
        "dashboard_portlet_${RANDOM.nextInt(32000)}"
    }
}
