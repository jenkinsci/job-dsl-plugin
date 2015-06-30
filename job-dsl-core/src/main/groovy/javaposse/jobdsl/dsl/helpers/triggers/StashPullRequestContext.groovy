package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

/**
 * DSL for https://github.com/jenkinsci/stash-pullrequest-builder-plugin
 */
class StashPullRequestContext implements Context {
    String cron = ''
    String stashHost = ''
    String username = ''
    String password = ''
    String projectCode = ''
    String repositoryName = ''
    String ciSkipPhrases = ''
    boolean checkDestinationCommit = false
    boolean checkMergeable = false
    boolean checkNotConflicted = false
    boolean onlyBuildOnComment = false
    String ciBuildPhrases = ''

    void cron(String cron) {
        this.cron = cron
    }

    void stashHost(String stashHost) {
        this.stashHost = stashHost
    }

    void username(String username) {
        this.username = username
    }

    void password(String password) {
        this.password = password
    }

    void projectCode(String projectCode) {
        this.projectCode = projectCode
    }

    void repositoryName(String repositoryName) {
        this.repositoryName = repositoryName
    }

    void ciSkipPhrases(String ciSkipPhrases) {
        this.ciSkipPhrases = ciSkipPhrases
    }

    void checkDestinationCommit(boolean checkDestinationCommit = false) {
        this.checkDestinationCommit = checkDestinationCommit
    }

    void checkMergeable(boolean checkMergeable = false) {
        this.checkMergeable = checkMergeable
    }

    void checkNotConflicted(boolean checkNotConflicted = false) {
        this.checkNotConflicted = checkNotConflicted
    }

    void onlyBuildOnComment(boolean onlyBuildOnComment = false) {
        this.onlyBuildOnComment = onlyBuildOnComment
    }

    void ciBuildPhrases(String ciBuildPhrases) {
        this.ciBuildPhrases = ciBuildPhrases
    }
}
