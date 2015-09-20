package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext

class PropertiesContext extends AbstractExtensibleContext {
    List<Node> propertiesNodes = []

    PropertiesContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        propertiesNodes << node
    }

    /**
     * Adds links in the sidebar of the project page.
     *
     * The icon may be a plain filename of an image in Jenkins' {@code images/24x24} directory (such as
     * {@code help.gif}, {@code document.gif} or {@code refresh.gif}), or {@code /userContent/filename.ext} for a custom
     * icon placed in the {@code JENKINS_HOME/userContent} directory. User content can also be uploaded by using
     * {@link javaposse.jobdsl.dsl.DslFactory#userContent(java.lang.String, java.io.InputStream)}.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'sidebar-link', minimumVersion = '1.7')
    void sidebarLinks(@DslContext(SidebarLinkContext) Closure sidebarLinkClosure) {
        SidebarLinkContext sidebarLinkContext = new SidebarLinkContext()
        ContextHelper.executeInContext(sidebarLinkClosure, sidebarLinkContext)

        propertiesNodes << new NodeBuilder().'hudson.plugins.sidebar__link.ProjectLinks' {
            links(sidebarLinkContext.links)
        }
    }

    /**
     * Allows to configure a custom icon for each job.
     *
     * The argument must point to a custom icon placed in the {@code JENKINS_HOME/userContent/customIcon} directory.
     * User content can be uploaded by using
     * {@link javaposse.jobdsl.dsl.DslFactory#userContent(java.lang.String, java.io.InputStream)}.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'custom-job-icon', minimumVersion = '0.2')
    void customIcon(String iconFileName) {
        Preconditions.checkNotNullOrEmpty(iconFileName, 'iconFileName must be specified')

        propertiesNodes << new NodeBuilder().'jenkins.plugins.jobicon.CustomIconProperty' {
            iconfile(iconFileName)
        }
    }

    /**
     * Changes the date pattern for the BUILD_ID or BUILD_TIMESTAMP variable.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'zentimestamp', minimumVersion = '3.3')
    void zenTimestamp(String pattern) {
        propertiesNodes << new NodeBuilder().'hudson.plugins.zentimestamp.ZenTimestampJobProperty' {
            changeBUILDID(true)
            delegate.pattern(pattern)
        }
    }
}
