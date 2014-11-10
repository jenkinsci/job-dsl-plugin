package javaposse.jobdsl.dsl.helpers.wrapper

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext.Timeout

/** Context to configure timeout */
class TimeoutContext implements Context {

    Timeout type
    int limit = 3

    int percentage = 150
    int numberOfBuilds = 3
    int minutesDefault = 60

    int noActivitySeconds = 180

    boolean failBuild = false
    boolean writeDescription = false
    String description = ''
    private final JobManagement jobManagement

    TimeoutContext(Timeout type, JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.type = type
    }

    /**
     * @deprecated for backwards compatibility
     */
    @Deprecated
    void limit(int limit) {
        Preconditions.checkArgument([Timeout.elastic, Timeout.absolute].contains(type))
        jobManagement.logDeprecationWarning()
        if (type == Timeout.absolute) {
            this.limit = limit
        } else if (type == Timeout.elastic) {
            this.minutesDefault = limit
        }
    }

    /**
     * @deprecated for backwards compatibility
     */
    @Deprecated
    void percentage(int percentage) {
        jobManagement.logDeprecationWarning()
        this.percentage = percentage
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

    /**
     * @deprecated for backwards compatibility
     */
    @Deprecated
    void writeDescription(boolean writeDesc = true) {
        jobManagement.logDeprecationWarning()
        this.writeDescription = writeDesc
    }

    void writeDescription(String description) {
        this.description = description
        this.writeDescription = true
    }
}
