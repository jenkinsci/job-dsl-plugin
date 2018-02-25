package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class NamedExceptionsBranchPropertyStrategyContext implements Context {
    List<Node> defaultProperties = []
    List<Node> namedExceptions = []

    /**
     * Default branch properties for unnamed branches.
     */
    void defaultProperties(@DslContext(PropertyStrategyContext) Closure propertyStrategyClosure) {
        PropertyStrategyContext context = new PropertyStrategyContext()
        ContextHelper.executeInContext(propertyStrategyClosure, context)

        this.defaultProperties = context.properties
    }

    /**
     * Named branch exception to the default properties.
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
