package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class NamedExceptionsBranchPropertyStrategyContext implements Context {
    List<Node> defaultProperties = []
    List<Node> namedExceptions = []

    /**
     * Default branch properties for unnamed branches.
     *
     * @since 1.69
     */
    void defaultProperties(@DslContext(PropertyStrategyContext) Closure propertyStrategyClosure) {
        PropertyStrategyContext context = new PropertyStrategyContext()
        ContextHelper.executeInContext(propertyStrategyClosure, context)

        this.defaultProperties = context.properties
    }

    /**
     * Named branch exception to the default properties.
     *
     * @since 1.69
     */
    void namedException(@DslContext(NamedExceptionBranchPropertyContext) Closure namedExceptionBranchPropertyClosure) {
        NamedExceptionBranchPropertyContext context = new NamedExceptionBranchPropertyContext()
        ContextHelper.executeInContext(namedExceptionBranchPropertyClosure, context)

        this.namedExceptions << new NodeBuilder().'jenkins.branch.NamedExceptionsBranchPropertyStrategy_-Named' {
            context.properties.size() > 0 ? props(context.properties) : props(class: 'empty-list')
            name(context.branch)
        }
    }
}
