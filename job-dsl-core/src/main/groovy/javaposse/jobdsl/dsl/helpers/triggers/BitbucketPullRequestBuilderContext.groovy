package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class BitbucketPullRequestBuilderContext implements Context {

    String cron = 'H/5 * * * *'
    String username = ''
    String password = ''
    String repositoryOwner = ''
    String repositoryName = ''
    String ciSkipPhases = ''
    boolean checkDestinationCommit = false

    void cron(String cron) {
        this.cron = cron
    }

    void username(String username) {
        this.username = username
    }

    void password(String password) {
        this.password = password
    }

    void repositoryOwner(String repositoryOwner) {
        this.repositoryOwner = repositoryOwner
    }

    void repositoryName(String repositoryName) {
        this.repositoryName = repositoryName
    }

    void ciSkipPhases(String ciSkipPhases) {
        this.ciSkipPhases = ciSkipPhases
    }

    void checkDestinationCommit(boolean checkDestinationCommit = false) {
        this.checkDestinationCommit = checkDestinationCommit
    }
}
