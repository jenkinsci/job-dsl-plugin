package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.Context

class ThrottleConcurrentBuildsContext implements Context {
    boolean throttleDisabled = false
    List<String> categories = []
    int maxConcurrentPerNode = 0
    int maxConcurrentTotal = 0

    /**
     * Disables the throttle. Defaults to {@code false}.
     */
    void throttleDisabled(boolean throttleDisabled = true) {
        this.throttleDisabled = throttleDisabled
    }

    /**
     * Throttles this project as part of one or more categories.
     */
    void categories(List<String> categories) {
        this.categories = categories
    }

    /**
     * Sets the maximum number of concurrent builds of this project (or category) to be allowed to run at any one time,
     * across all nodes.
     */
    void maxPerNode(int maxPerNode) {
        this.maxConcurrentPerNode = maxPerNode
    }

    /**
     * Sets the maximum number of concurrent builds of this project (or category) to be allowed to run per node.
     */
    void maxTotal(int maxTotal) {
        this.maxConcurrentTotal = maxTotal
    }
}
