package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context

/**
 * <p>DSL support for the m2release plugin.</p>
 * <p><a href="https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin">M2 Release Plugin</a></p>
 */
class MavenReleaseContext implements Context {
    String scmUserEnvVar = ''
    String scmPasswordEnvVar = ''
    String releaseEnvVar = 'IS_M2RELEASEBUILD'
    String releaseGoals = '-Dresume=false release:prepare release:perform'
    String dryRunGoals = '-Dresume=false -DdryRun=true release:prepare'
    boolean selectCustomScmCommentPrefix = false
    boolean selectAppendJenkinsUsername = false
    boolean selectScmCredentials = false
    int numberOfReleaseBuildsToKeep = 1

    /**
     * <p>If defined, an environment variable with this name will hold the scm username when triggering a
     * release build (this is the username the user enters when triggering a release build, not the username
     * given to Jenkins' SCM configuration of the job).</p>
     * @param scmUserEnvVar (default: &laquo;empty&raquo;)
     */
    void scmUserEnvVar(String scmUserEnvVar) {
        this.scmUserEnvVar = scmUserEnvVar
    }

    /**
     * <p>If defined, an environment variable with this name will hold the scm password when triggering a
     * release build (this is the password the user enters when triggering a release build, not the password
     * given to Jenkins' SCM configuration of the job).</p>
     * <p>As the passed passwords would potentially get written to the logs and therefore visible to users,
     * we recommend you to install the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin">Mask Password Plugin</a>.</p>
     * @param scmPasswordEnvVar (default: &laquo;empty&raquo;)
     */
    void scmPasswordEnvVar(String scmPasswordEnvVar) {
        this.scmPasswordEnvVar = scmPasswordEnvVar
    }

    /**
     * <p>An environment variable with this name indicates whether the current build is a release build or not.
     * This can be used e.g. within a shell or the conditional buildstep to do pre and post release processing.
     * The value will be boolean (true if it is a release build, false if its not a release build).</p>
     * @param releaseEnvVar (default: "IS_M2RELEASEBUILD")
     */
    void releaseEnvVar(String releaseEnvVar) {
        this.releaseEnvVar = releaseEnvVar
    }

    /**
     * <p>Enter the goals you wish to use as part of the release process. e.g. "release:prepare release:perform"</p>
     * @param releaseGoals (default: "-Dresume=false release:prepare release:perform")
     */
    void releaseGoals(String releaseGoals) {
        this.releaseGoals = releaseGoals
    }

    /**
     * <p>Enter the goals you wish to use as part of the 'dryRun' - to simulate the release build.
     * e.g. "release:prepare -DdryRun=true"</p>
     * @param dryRunGoals (default: "-Dresume=false -DdryRun=true release:prepare")
     */
    void dryRunGoals(String dryRunGoals) {
        this.dryRunGoals = dryRunGoals
    }

    /**
     * <p>Enable this to have the "Select custom SCM comment prefix" option selected by default
     * in the "Perform Maven Release" view.</p>
     * @param selectCustomScmCommentPrefix (default: false)
     */
    void selectCustomScmCommentPrefix(boolean selectCustomScmCommentPrefix = true) {
        this.selectCustomScmCommentPrefix = selectCustomScmCommentPrefix
    }

    /**
     * <p>Enable this to have the "Append Jenkins Username" option (part of the "Specify custom SCM comment prefix"
     * configuration) selected by default in the "Perform Maven Release" view.</p>
     * @param selectAppendHudsonUsername (default: false)
     */
    void selectAppendJenkinsUsername(boolean selectAppendJenkinsUsername = true) {
        this.selectAppendJenkinsUsername = selectAppendJenkinsUsername
    }

    /**
     * <p>Enable this to have the "specify SCM login/password" option selected by default in the
     * "Perform Maven Release" view.</p>
     * @param selectScmCredentials (default: false)
     */
    void selectScmCredentials(boolean selectScmCredentials = true) {
        this.selectScmCredentials = selectScmCredentials
    }

    /**
     * <p>Specify the number of successful release builds to keep forever. A value of -1 will lock all successful
     * release builds, 0 will not lock any builds.</p>
     * @param numberOfReleaseBuildsToKeep (default: 1)
     */
    void numberOfReleaseBuildsToKeep(int numberOfReleaseBuildsToKeep) {
        this.numberOfReleaseBuildsToKeep = numberOfReleaseBuildsToKeep
    }
}
