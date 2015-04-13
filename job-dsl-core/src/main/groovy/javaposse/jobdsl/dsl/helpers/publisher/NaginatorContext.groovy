package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class NaginatorContext implements Context {
    boolean rerunIfUnstable
    int retryLimit
    Node delay

    NaginatorContext() {
        progressiveDelay(5 * 60, 3 * 60 * 60)
    }

    void rerunIfUnstable(boolean rerunIfUnstable = true) {
        this.rerunIfUnstable = rerunIfUnstable
    }

    void retryLimit(int retryLimit) {
        this.retryLimit = retryLimit
    }

    void progressiveDelay(int increment, int max) {
        this.delay = new NodeBuilder().delay(class: 'com.chikli.hudson.plugin.naginator.ProgressiveDelay') {
            delegate.increment(increment)
            delegate.max(max)
        }
    }

    void fixedDelay(int delay) {
        this.delay = new NodeBuilder().delay(class: 'com.chikli.hudson.plugin.naginator.FixedDelay') {
            delegate.delay(delay)
        }
    }
}
