package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

class CpsContext implements Context {
    String script
    boolean sandbox

    void script(String script) {
        this.script = script
    }

    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }
}
