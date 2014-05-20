package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.helpers.Context


class ThrottleConcurrentBuildsContext implements Context {
    def throttleDisabled = false
    def categories = []
    def maxConcurrentPerNode = 0
    def maxConcurrentTotal = 0

    def throttleDisabled(boolean throttleDisabled = true) {
        this.throttleDisabled = throttleDisabled
    }

    def categories(List<String> categories) {
        this.categories = categories
    }

    def maxPerNode(int maxPerNode) {
        this.maxConcurrentPerNode = maxPerNode
    }

    def maxTotal(int maxTotal) {
        this.maxConcurrentTotal = maxTotal
    }
}
