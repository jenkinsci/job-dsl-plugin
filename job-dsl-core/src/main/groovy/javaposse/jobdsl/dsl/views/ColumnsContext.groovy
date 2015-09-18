package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class ColumnsContext extends AbstractContext {
    List<Node> columnNodes = []

    ColumnsContext(JobManagement jobManagement) {
        super(jobManagement)
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
     *
     * @since 1.31
     */
    @RequiresPlugin(id = 'build-node-column', minimumVersion = '0.1')
    void lastBuildNode() {
        columnNodes << new Node(null, 'org.jenkins.plugins.column.LastBuildNodeColumn')
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
     * Adds a column showing showing job's cron trigger expression.
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
}
