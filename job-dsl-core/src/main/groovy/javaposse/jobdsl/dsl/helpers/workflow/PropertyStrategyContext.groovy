package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Context

class PropertyStrategyContext implements Context {
    List<Node> properties = []

    /**
     * Branch source property that suppresses automatically building indexed branches.
     */
    void noTriggerBranchProperty() {
        properties << new NodeBuilder().'jenkins.branch.NoTriggerBranchProperty'()
    }
}
