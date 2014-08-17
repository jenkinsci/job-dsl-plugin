package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.JobManagement

class StepContext extends AbstractStepContext {
    StepContext(List<Node> stepNodes = [], JobManagement jobManagement) {
        super(stepNodes, jobManagement)
    }
}
