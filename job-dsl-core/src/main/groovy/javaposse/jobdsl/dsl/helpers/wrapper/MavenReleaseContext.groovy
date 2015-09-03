package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

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
     * If defined, an environment variable with this name will hold the scm username when triggering a
     * release build (this is the username the user enters when triggering a release build, not the username
     * given to Jenkins' SCM configuration of the job). Empty by default.
     */
    void scmUserEnvVar(String scmUserEnvVar) {
        this.scmUserEnvVar = scmUserEnvVar
    }

    /**
     * If defined, an environment variable with this name will hold the scm password when triggering a
     * release build (this is the password the user enters when triggering a release build, not the password
     * given to Jenkins' SCM configuration of the job). Empty by default.
     *
     * As the passed passwords would potentially get written to the logs and therefore visible to users,
     * it is recommended to install the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Mask+Passwords+Plugin">Mask Password Plugin</a>.
     */
    void scmPasswordEnvVar(String scmPasswordEnvVar) {
        this.scmPasswordEnvVar = scmPasswordEnvVar
    }

    /**
     * An environment variable with this name indicates whether the current build is a release build or not.
     * This can be used e.g. within a shell or the conditional buildstep to do pre and post release processing.
     * The value will be boolean (true if it is a release build, false if its not a release build). Defaults to
     * {@code 'IS_M2RELEASEBUILD'}.
     */
    void releaseEnvVar(String releaseEnvVar) {
        this.releaseEnvVar = releaseEnvVar
    }

    /**
     * Enter the goals you wish to use as part of the release process. Defaults to
     * {@code '-Dresume=false release:prepare release:perform'}.
     */
    void releaseGoals(String releaseGoals) {
        this.releaseGoals = releaseGoals
    }

    /**
     * Enter the goals you wish to use as part of the dry run to simulate the release build. Defaults to
     * {@code -Dresume=false -DdryRun=true release:prepare}.
     */
    void dryRunGoals(String dryRunGoals) {
        this.dryRunGoals = dryRunGoals
    }

    /**
     * Enable this to have the "Select custom SCM comment prefix" option selected by default
     * in the "Perform Maven Release" view. Defaults to {@code false}.
     */
    void selectCustomScmCommentPrefix(boolean selectCustomScmCommentPrefix = true) {
        this.selectCustomScmCommentPrefix = selectCustomScmCommentPrefix
    }

    /**
     * Enable this to have the "Append Jenkins Username" option (part of the "Specify custom SCM comment prefix"
     * configuration) selected by default in the "Perform Maven Release" view. Defaults to {@code false}.
     */
    void selectAppendJenkinsUsername(boolean selectAppendJenkinsUsername = true) {
        this.selectAppendJenkinsUsername = selectAppendJenkinsUsername
    }

    /**
     * Enable this to have the "specify SCM login/password" option selected by default in the
     * "Perform Maven Release" view. Defaults to {@code false}.
     */
    void selectScmCredentials(boolean selectScmCredentials = true) {
        this.selectScmCredentials = selectScmCredentials
    }

    /**
     * Specify the number of successful release builds to keep forever. A value of -1 will lock all successful
     * release builds, 0 will not lock any builds. Defaults to 1.
     */
    void numberOfReleaseBuildsToKeep(int numberOfReleaseBuildsToKeep) {
        this.numberOfReleaseBuildsToKeep = numberOfReleaseBuildsToKeep
    }
}
