package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import java.security.SecureRandom

class DashboardPortletContext implements Context {
    private static final Random RANDOM = new SecureRandom()

    protected final List<Node> portletNodes = []

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

    private static String generatePortletId() {
        "dashboard_portlet_${RANDOM.nextInt(32000)}"
    }
}
