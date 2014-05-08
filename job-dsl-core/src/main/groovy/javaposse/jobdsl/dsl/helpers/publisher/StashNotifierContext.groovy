package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

/**
 * DSL for https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin
 */
class StashNotifierContext implements Context {

    String commitSha1 = ""
    boolean keepRepeatedBuilds = false

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
