package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class NaginatorContext implements Context {
    boolean rerunIfUnstable
    int retryLimit
    Node delay

    NaginatorContext() {
        progressiveDelay(5 * 60, 3 * 60 * 60)
    }

    /**
     * If set, reschedules the build also when the build is unstable. Defaults to {@code false}.
     */
    void rerunIfUnstable(boolean rerunIfUnstable = true) {
        this.rerunIfUnstable = rerunIfUnstable
    }

    /**
     * Sets a retry limit. Defaults to 0.
     */
    void retryLimit(int retryLimit) {
        this.retryLimit = retryLimit
    }

    /**
     * Specifies an increasing delay between re-runs. The parameters are specified in seconds.
     */
    void progressiveDelay(int increment, int max) {
        this.delay = new NodeBuilder().delay(class: 'com.chikli.hudson.plugin.naginator.ProgressiveDelay') {
            delegate.increment(increment)
            delegate.max(max)
        }
    }

    /**
     * Specifies a fixed delay between re-runs. The delay is specified in seconds.
     */
    void fixedDelay(int delay) {
        this.delay = new NodeBuilder().delay(class: 'com.chikli.hudson.plugin.naginator.FixedDelay') {
            delegate.delay(delay)
        }
    }
}
