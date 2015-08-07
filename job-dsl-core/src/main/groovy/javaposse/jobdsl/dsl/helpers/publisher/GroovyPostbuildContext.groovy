package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class GroovyPostbuildContext extends AbstractContext {
    String script
    PublisherContext.Behavior behavior = PublisherContext.Behavior.DoNothing
    boolean sandbox

    GroovyPostbuildContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void script(String script) {
        this.script = script
    }

    void behavior(PublisherContext.Behavior behavior) {
        this.behavior = behavior
    }

    @RequiresPlugin(id = 'groovy-postbuild', minimumVersion = '2.2')
    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }
}
