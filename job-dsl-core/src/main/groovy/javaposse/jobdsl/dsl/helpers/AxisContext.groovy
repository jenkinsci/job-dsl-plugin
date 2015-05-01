package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractExtensibleContext


class AxisContext extends AbstractExtensibleContext {
    List<Node> axisNodes = []
    List<Closure> configureBlocks = []

    AxisContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    protected void addExtensionNode(Node node) {
        axisNodes << node
    }

    void text(String axisName, String... axisValues) {
        text(axisName, axisValues.toList())
    }

    void text(String axisName, Iterable<String> axisValues) {
        simpleAxis('Text', axisName, axisValues)
    }

    void label(String axisName, String... axisValues) {
        label(axisName, axisValues.toList())
    }

    void label(String axisName, Iterable<String> axisValues) {
        simpleAxis('Label', axisName, axisValues)
    }

    void labelExpression(String axisName, String... axisValues) {
        labelExpression(axisName, axisValues.toList())
    }

    void labelExpression(String axisName, Iterable<String> axisValues) {
        simpleAxis('LabelExp', axisName, axisValues)
    }

    void jdk(String... axisValues) {
        jdk(axisValues.toList())
    }

    void jdk(Iterable<String> axisValues) {
        simpleAxis('JDK', 'jdk', axisValues)
    }

    void configure(Closure closure) {
        configureBlocks << closure
    }

    private simpleAxis(String axisType, String axisName, Iterable<String> axisValues) {
        NodeBuilder nodeBuilder = new NodeBuilder()

        axisNodes << nodeBuilder."hudson.matrix.${axisType}Axis" {
            name axisName
            values {
                axisValues.each { string it }
            }
        }
    }
}
