package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context


/** Context to configure timeout */
class TimeoutContext implements Context {

    WrapperContext.Timeout type
    def limit = 3
    def failBuild = false
    def writeDescription = false
    def percentage = 0

    TimeoutContext(WrapperContext.Timeout type) {
        this.type = type
    }

    def limit(int limit) {
        this.limit = limit
    }

    def failBuild(boolean fail) {
        this.failBuild = fail
    }

    def writeDescription(boolean writeDesc) {
        this.writeDescription = writeDesc
    }

    def percentage(int percentage) {
        this.percentage = percentage
    }

}
