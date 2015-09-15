package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class SonarContext implements Context {
    String branch
    String language
    String mavenOpts
    String jobAdditionalProperties
    String rootPom
    boolean overrideTriggers
    final SonarTriggersContext sonarTriggersContext = new SonarTriggersContext()

    /**
     * Sets the {@code sonar.branch} property.
     */
    void branch(String branch) {
        this.branch = branch
    }

    /**
     * Sets the {@code sonar.language} property.
     */
    void language(String language) {
        this.language = language
    }

    /**
     * Sets the {@code sonar.mavenOpts} property.
     */
    void mavenOpts(String mavenOpts) {
        this.mavenOpts = mavenOpts
    }

    /**
     * Sets the {@code sonar.jobAdditionalProperties} property.
     */
    void jobAdditionalProperties(String jobAdditionalProperties) {
        this.jobAdditionalProperties = jobAdditionalProperties
    }

    /**
     * Sets the {@code sonar.rootPom} property.
     */
    void rootPom(String rootPom) {
        this.rootPom = rootPom
    }

    /**
     * Overrides the default trigger actions set at SonarQube installation level.
     */
    void overrideTriggers(@DslContext(SonarTriggersContext) Closure sonarTriggersClosure) {
        overrideTriggers = true
        ContextHelper.executeInContext(sonarTriggersClosure, sonarTriggersContext)
    }
}
