package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class SonarContext implements Context {
    String branch
    boolean overrideTriggers
    final SonarTriggersContext sonarTriggersContext = new SonarTriggersContext()

    void branch(String branch) {
        this.branch = branch
    }

    void overrideTriggers(@DslContext(SonarTriggersContext) Closure sonarTriggersClosure) {
        overrideTriggers = true
        ContextHelper.executeInContext(sonarTriggersClosure, sonarTriggersContext)
    }
}
