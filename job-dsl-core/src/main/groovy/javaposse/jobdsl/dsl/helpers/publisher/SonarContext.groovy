package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class SonarContext implements Context {
    String branch
    boolean overrideTriggers
    final SonarTriggersContext sonarTriggersContext = new SonarTriggersContext()

    /**
     * Sets the {@code sonar.branch} property.
     */
    void branch(String branch) {
        this.branch = branch
    }

    /**
     * Overrides the default trigger actions set at SonarQube installation level.
     */
    void overrideTriggers(@DslContext(SonarTriggersContext) Closure sonarTriggersClosure) {
        overrideTriggers = true
        ContextHelper.executeInContext(sonarTriggersClosure, sonarTriggersContext)
    }
}
