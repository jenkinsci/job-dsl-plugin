package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class GitBranchSourceContext implements Context {
    String id = UUID.randomUUID()
    String remote
    String credentialsId
    String includes = '*'
    String excludes
    boolean ignoreOnPushNotifications
    Node branchPropertyStrategy

    /**
     * Specifies a unique ID for this branch source.
     *
     * @since 1.62
     */
    void id(String id) {
        this.id = id
    }

    /**
     * Sets the Git remote repository URL.
     */
    void remote(String remote) {
        this.remote = remote
    }

    /**
     * Sets credentials for authentication with the remote repository.
     */
    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    /**
     * Sets a pattern for branches to include.
     */
    void includes(String includes) {
        this.includes = includes
    }

    /**
     * Sets a pattern for branches to exclude.
     */
    void excludes(String excludes) {
        this.excludes = excludes
    }

    /**
     * If set, ignores push notifications. Defaults to {@code false}.
     */
    void ignoreOnPushNotifications(boolean ignoreOnPushNotifications = true) {
        this.ignoreOnPushNotifications = ignoreOnPushNotifications
    }

    /**
     * Use the default branch property strategy for this branch source.
     *
     * @since 1.69
     */
    void defaultBranchPropertyStrategy(@DslContext(PropertyStrategyContext) Closure propertyStrategyClosure) {
        PropertyStrategyContext context = new PropertyStrategyContext()
        ContextHelper.executeInContext(propertyStrategyClosure, context)

        this.branchPropertyStrategy =
            new NodeBuilder().strategy(class: 'jenkins.branch.DefaultBranchPropertyStrategy') {
                properties(context.properties)
            }
    }

    /**
     * Use the named exceptions branch property strategy for this branch source.
     *
     * @since 1.69
     */
    void namedExceptionsBranchPropertyStrategy(
        @DslContext(NamedExceptionsBranchPropertyStrategyContext)
        Closure namedExceptionsBranchPropertyStrategyClosure) {
        NamedExceptionsBranchPropertyStrategyContext context = new NamedExceptionsBranchPropertyStrategyContext()
        ContextHelper.executeInContext(namedExceptionsBranchPropertyStrategyClosure, context)

        this.branchPropertyStrategy =
            new NodeBuilder().strategy(class: 'jenkins.branch.NamedExceptionsBranchPropertyStrategy') {
                defaultProperties(context.defaultProperties)
                namedExceptions(context.namedExceptions)
            }
    }
}
