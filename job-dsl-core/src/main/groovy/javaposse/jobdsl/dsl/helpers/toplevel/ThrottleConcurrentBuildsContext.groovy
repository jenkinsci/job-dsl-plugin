package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context

class ThrottleConcurrentBuildsContext implements Context {
    boolean throttleDisabled = false
    List<String> categories = []
    int maxConcurrentPerNode = 0
    int maxConcurrentTotal = 0

    void throttleDisabled(boolean throttleDisabled = true) {
        this.throttleDisabled = throttleDisabled
    }

    void categories(List<String> categories) {
        this.categories = categories
    }

    void maxPerNode(int maxPerNode) {
        this.maxConcurrentPerNode = maxPerNode
    }

    void maxTotal(int maxTotal) {
        this.maxConcurrentTotal = maxTotal
    }
}
