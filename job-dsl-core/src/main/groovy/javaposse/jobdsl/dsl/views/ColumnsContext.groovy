package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

@ContextType('hudson.views.ListViewColumn')
class ColumnsContext extends AbstractExtensibleContext {
    List<Node> columnNodes = []

    ColumnsContext(JobManagement jobManagement) {
        super(jobManagement, null)
    }

    @Override
    protected void addExtensionNode(Node node) {
        columnNodes << node
    }

    /**
     * Adds a column showing the status of the last build.
     */
    void status() {
        columnNodes << new Node(null, 'hudson.views.StatusColumn')
    }

    /**
     * Adds a weather report showing the aggregated status of recent builds.
     */
    void weather() {
        columnNodes << new Node(null, 'hudson.views.WeatherColumn')
    }

    /**
     * Adds a column showing the item name.
     */
    void name() {
        columnNodes << new Node(null, 'hudson.views.JobColumn')
    }

    /**
     * Adds a column showing the last successful build.
     */
    void lastSuccess() {
        columnNodes << new Node(null, 'hudson.views.LastSuccessColumn')
    }

    /**
     * Adds a column showing the last failed build.
     */
    void lastFailure() {
        columnNodes << new Node(null, 'hudson.views.LastFailureColumn')
    }

    /**
     * Adds a column showing the duration of the last build.
     */
    void lastDuration() {
        columnNodes << new Node(null, 'hudson.views.LastDurationColumn')
    }

    /**
     * Adds a column showing a button for scheduling a build.
     */
    void buildButton() {
        columnNodes << new Node(null, 'hudson.views.BuildButtonColumn')
    }

    /**
     * Adds a column showing a button for opening the console output.
     *
     * @since 1.23
     */
    @RequiresPlugin(id = 'extra-columns')
    void lastBuildConsole() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.LastBuildConsoleColumn')
    }

    /**
     * Adds a column showing a button for opening the item's configuration.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'extra-columns')
    void configureProject() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.ConfigureProjectColumn')
    }

    /**
     * Adds a column showing test results.
     *
     * @since 1.49
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.6')
    void testResult(int format) {
        columnNodes << new NodeBuilder().'jenkins.plugins.extracolumns.TestResultColumn' {
            testResultFormat(format)
        }
    }

    /**
     * Adds a column for showing that a build has been claimed.
     *
     * @since 1.29
     */
    @RequiresPlugin(id = 'claim')
    void claim() {
        columnNodes << new Node(null, 'hudson.plugins.claim.ClaimColumn')
    }

    /**
     * Adds a column for showing the node which executed the last build.
     * Requires version 1.16 or later of the Extra Columns plugin.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.16')
    void lastBuildNode() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.LastBuildNodeColumn')
    }

    /**
     * Adds a column for showing a job's category.
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'categorized-view', minimumVersion = '1.8')
    void categorizedJob() {
        columnNodes << new Node(null, 'org.jenkinsci.plugins.categorizedview.IndentedJobColumn')
    }

    /**
     * Adds a column for showing Robot Framework test results.
     * @since 1.33
     */
    @RequiresPlugin(id = 'robot', minimumVersion = '1.6.0')
    void robotResults() {
        columnNodes << new Node(null, 'hudson.plugins.robot.view.RobotListViewColumn')
    }

    /**
     * Adds a column showing a custom icon.
     *
     * @since 1.33
     */
    @RequiresPlugin(id = 'custom-job-icon', minimumVersion = '0.2')
    void customIcon() {
        columnNodes << new Node(null, 'jenkins.plugins.jobicon.CustomIconColumn')
    }

    /**
     * Adds a column showing job's cron trigger expression.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'cron_column', minimumVersion = '1.4')
    void cronTrigger() {
        columnNodes << new Node(null, 'hudson.plugins.CronViewColumn')
    }

    /**
     * Adds a column showing job's progress bar.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'progress-bar-column-plugin', minimumVersion = '1.0')
    void progressBar() {
        columnNodes << new Node(null, 'org.jenkins.ci.plugins.progress__bar.ProgressBarColumn')
    }

    /**
     * Adds a column showing a release button.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'release', minimumVersion = '2.3')
    void releaseButton() {
        columnNodes << new Node(null, 'hudson.plugins.release.ReleaseButtonColumn')
    }

    /**
     * Adds a column showing JaCoCo line coverage.
     *
     * @since 1.43
     */
    @RequiresPlugin(id = 'jacoco', minimumVersion = '1.0.10')
    void jacoco() {
        columnNodes << new Node(null, 'hudson.plugins.jacococoveragecolumn.JaCoCoColumn')
    }

    /**
     * Adds a column showing build processor or build processor label restrictions of a job.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.14')
    void slaveOrLabel() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.SlaveOrLabelColumn')
    }

    /**
     * Adds a column showing the name of the user that started the last build.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.16')
    void userName() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.UserNameColumn')
    }

    /**
     * Adds a column showing the date of the last job configuration modification.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.14')
    void lastConfigurationModification() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.LastJobConfigurationModificationColumn')
    }

    /**
     * Adds a column showing a single build parameter or all build parameters of the current/last build.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.13')
    void buildParameters(String parameter = null) {
        columnNodes << new NodeBuilder().'jenkins.plugins.extracolumns.BuildParametersColumn' {
            singlePara(parameter as boolean)
            parameterName(parameter ?: '')
        }
    }

    /**
     * Adds a column showing a link to the workspace.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.15')
    void workspace() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.WorkspaceColumn')
    }

    /**
     * Adds a column showing a button or an icon for disabling/enabling a project.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.7')
    void disableProject(boolean icon = false) {
        columnNodes << new NodeBuilder().'jenkins.plugins.extracolumns.DisableProjectColumn' {
            useIcon(icon)
        }
    }

    /**
     * Adds a column showing job's next launch.
     *
     * @since 1.53
     */
    @RequiresPlugin(id = 'next-executions', minimumVersion = '1.0.12')
    void nextLaunch() {
        columnNodes << new NodeBuilder().'hudson.plugins.nextexecutions.columns.NextExecutionColumn' {
          triggerClass('hudson.triggers.TimerTrigger')
        }
    }

    /**
     * Adds a column showing job's next possible launch.
     *
     * @since 1.53
     */
    @RequiresPlugin(id = 'next-executions', minimumVersion = '1.0.12')
    void nextPossibleLaunch() {
        columnNodes << new NodeBuilder().'hudson.plugins.nextexecutions.columns.PossibleNextExecutionColumn' {
          triggerClass('hudson.triggers.SCMTrigger')
        }
    }

    /**
     * Adds a column showing the type of source code management that is used in the project.
     *
     * @since 1.56
     */
    @RequiresPlugin(id = 'extra-columns', minimumVersion = '1.4')
    void scmType() {
        columnNodes << new Node(null, 'jenkins.plugins.extracolumns.SCMTypeColumn')
    }
}
