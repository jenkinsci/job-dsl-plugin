package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class PeriodicFolderTriggerContext implements Context {
    String spec = 'H * * * *'
    int interval = 3600000

    /**
     * Set the spec for the trigger.
     *
     * @since 1.41
     */
    void spec(String spec) {
        this.spec = spec
    }

    /**
     * Set the interval for the trigger.
     *
     * @since 1.41
     */
    void interval(int interval) {
        this.interval = interval
    }
}
