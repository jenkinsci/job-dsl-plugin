package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout

/**
 * Context to configure build timeouts.
 */
class TimeoutContext implements Context {
    private final JobManagement jobManagement

    Timeout type = Timeout.absolute
    int limit = 3

    int percentage = 150
    int numberOfBuilds = 3
    int minutesDefault = 60

    int noActivitySeconds = 180

    boolean failBuild = false
    boolean writeDescription = false
    String description = ''

    TimeoutContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void elastic(int percentage = 150, int numberOfBuilds = 3, int minutesDefault = 60) {
        type = Timeout.elastic
        this.percentage = percentage
        this.numberOfBuilds = numberOfBuilds
        this.minutesDefault = minutesDefault
    }

    void noActivity(int seconds = 180) {
        jobManagement.requireMinimumPluginVersion('build-timeout', '1.13')
        type = Timeout.noActivity
        this.noActivitySeconds = seconds
    }

    void absolute(int minutes = 3) {
        type = Timeout.absolute
        this.limit = minutes
    }

    void likelyStuck() {
        type = Timeout.likelyStuck
    }

    void failBuild(boolean fail = true) {
        this.failBuild = fail
    }

    void writeDescription(String description) {
        this.description = description
        this.writeDescription = true
    }
}
