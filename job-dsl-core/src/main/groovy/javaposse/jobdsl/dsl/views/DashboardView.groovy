package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.views.portlets.DashboardPortletContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class DashboardView extends ListView {
    DashboardView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Adds portlets to the top of the page.
     */
    void topPortlets(@DslContext(DashboardPortletContext) Closure closure) {
        addPortlets('topPortlets', closure)
    }

    /**
     * Adds portlets to the bottom of the page.
     */
    void bottomPortlets(@DslContext(DashboardPortletContext) Closure closure) {
        addPortlets('bottomPortlets', closure)
    }

    /**
     * Adds portlets to the left column.
     */
    void leftPortlets(@DslContext(DashboardPortletContext) Closure closure) {
        addPortlets('leftPortlets', closure)
    }

    /**
     * Adds portlets to the right column.
     */
    void rightPortlets(@DslContext(DashboardPortletContext) Closure closure) {
        addPortlets('rightPortlets', closure)
    }

    protected void addPortlets(String elementName, Closure closure) {
        DashboardPortletContext context = new DashboardPortletContext(jobManagement)
        executeInContext(closure, context)

        configure {
            context.portletNodes.each { node ->
                it / "$elementName" << node
            }
        }
    }
}
