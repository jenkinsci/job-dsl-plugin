package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

/**
 * DSL for https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin
 */
class StashNotifierContext implements Context {

    String url = ""
    String username = ""
    String password = ""
    String commitSha1 = ""
    boolean ignoreUnverifiedSSL = false
    boolean keepRepeatedBuilds = false

    /**
     * <project>
     *     <publishers>
     *         <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *             <stashServerBaseUrl></stashServerBaseUrl>
     *
     * @param url
     */
    void url(String url) {
        this.url = url
    }

    /**
     * <project>
     *     <publishers>
     *         <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *             <stashUserName></stashUserName>
     *
     * @param username
     */
    void username(String username) {
        this.username = username
    }

    /**
     * <project>
     *     <publishers>
     *         <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *             <stashUserPassword></stashUserPassword>
     *
     * @param password
     */
    void password(String password) {
        this.password = password
    }

    /**
     * <project>
     *     <publishers>
     *         <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *             <ignoreUnverifiedSSLPeer>false</ignoreUnverifiedSSLPeer>
     *
     * @param ignoreUnverifiedSSL
     */
    void ignoreUnverifiedSSL(boolean ignoreUnverifiedSSL = true) {
        this.ignoreUnverifiedSSL = ignoreUnverifiedSSL
    }

    /**
     * <project>
     *     <publishers>
     *         <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *             <commitSha1></commitSha1>
     *
     * @param commitSha1
     */
    void commitSha1(String commitSha1) {
        this.commitSha1 = commitSha1
    }

    /**
     * <project>
     *     <publishers>
     *         <org.jenkinsci.plugins.stashNotifier.StashNotifier>
     *             <includeBuildNumberInKey>false</includeBuildNumberInKey>
     *
     * @param keepRepeatedBuilds
     */
    void keepRepeatedBuilds(boolean keepRepeatedBuilds = true) {
        this.keepRepeatedBuilds = keepRepeatedBuilds
    }
}
