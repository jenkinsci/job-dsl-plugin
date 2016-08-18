package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.AbstractExtensibleContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

@ContextType('hudson.model.JobProperty')
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
        executeInContext(sidebarLinkClosure, sidebarLinkContext)

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

    /**
     * Allows to configure job rebuild behaviour.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'rebuild', minimumVersion = '1.25')
    void rebuild(@DslContext(RebuildContext) Closure rebuildClosure) {
        RebuildContext rebuildContext = new RebuildContext()
        executeInContext(rebuildClosure, rebuildContext)

        propertiesNodes << new NodeBuilder().'com.sonyericsson.rebuild.RebuildSettings' {
            autoRebuild(rebuildContext.autoRebuild)
            rebuildDisabled(rebuildContext.rebuildDisabled)
        }
    }

    /**
     * Allows to configure job ownership.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'ownership', minimumVersion = '0.8')
    void ownership(@DslContext(OwnershipContext) Closure ownershipClosure) {
        OwnershipContext ownershipContext = new OwnershipContext()
        executeInContext(ownershipClosure, ownershipContext)

        propertiesNodes << new NodeBuilder().'com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerJobProperty' {
            delegate.ownership {
                ownershipEnabled(true)
                primaryOwnerId(ownershipContext.primaryOwnerId ?: '')
                coownersIds(class: 'sorted-set') {
                    ownershipContext.coOwnerIds.each { String coOwnerId ->
                        string(coOwnerId ?: '')
                    }
                }
            }
        }
    }

    /**
     * Configures the GitHub project URL.
     *
     * The URL will be set automatically when using the
     * {@link javaposse.jobdsl.dsl.helpers.ScmContext#github(java.lang.String)} or
     * {@link javaposse.jobdsl.dsl.helpers.scm.RemoteContext#github(java.lang.String)} methods.
     *
     * @since 1.40
     */
    @RequiresPlugin(id = 'github', minimumVersion = '1.12.0')
    void githubProjectUrl(String projectUrl) {
        propertiesNodes << new NodeBuilder().'com.coravy.hudson.plugins.github.GithubProjectProperty' {
            delegate.projectUrl(projectUrl ?: '')
        }
    }

    /**
     * Analyzes the causes of failed builds and presents the causes on the build page.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'build-failure-analyzer', minimumVersion = '1.13.2')
    void buildFailureAnalyzer(boolean scan = true) {
        propertiesNodes << new NodeBuilder().'com.sonyericsson.jenkins.plugins.bfa.model.ScannerJobProperty' {
            doNotScan(!scan)
        }
    }

    /**
     * Sets the priority of the job.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'PrioritySorter', minimumVersion = '3.4')
    void priority(int value) {
        propertiesNodes << new NodeBuilder().'jenkins.advancedqueue.priority.strategy.PriorityJobProperty' {
            useJobPriority(true)
            delegate.priority(value)
        }
    }

    /**
     * Configures job appearance for wall display.
     *
     * @since 1.46
     */
    @RequiresPlugin(id = 'jenkinswalldisplay', minimumVersion = '0.6.30')
    void wallDisplay(@DslContext(WallDisplayContext) Closure wallDisplayClosure) {
        WallDisplayContext wallDisplayContext = new WallDisplayContext()
        executeInContext(wallDisplayClosure, wallDisplayContext)

        propertiesNodes << new NodeBuilder().'de.pellepelster.jenkins.walldisplay.WallDisplayJobProperty' {
            if (wallDisplayContext.name) {
                wallDisplayName(wallDisplayContext.name)
            }
            if (wallDisplayContext.backgroundPicture) {
                wallDisplayBgPicture(wallDisplayContext.backgroundPicture)
            }
        }
    }
}
