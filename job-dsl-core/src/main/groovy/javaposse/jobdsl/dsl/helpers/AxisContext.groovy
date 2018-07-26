package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

@ContextType('hudson.matrix.Axis')
class AxisContext extends AbstractExtensibleContext {
    List<Node> axisNodes = []
    List<Closure> configureBlocks = []

    AxisContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Adds a user-defined axis. Can be called multiple times to add more axes.
     */
    void text(String axisName, String... axisValues) {
        text(axisName, axisValues.toList())
    }

    /**
     * Adds a user-defined axis. Can be called multiple times to add more axes.
     */
    void text(String axisName, Iterable<String> axisValues) {
        simpleAxis('Text', axisName, axisValues)
    }

    /**
     * Adds an axis that allows to run the same build on multiple nodes.
     */
    void label(String axisName, String... axisValues) {
        label(axisName, axisValues.toList())
    }

    /**
     * Adds an axis that allows to run the same build on multiple nodes.
     */
    void label(String axisName, Iterable<String> axisValues) {
        simpleAxis('Label', axisName, axisValues)
    }

    /**
     * Adds an axis that allows to run the same build on multiple nodes by evaluating a boolean expression.
     */
    void labelExpression(String axisName, String... axisValues) {
        labelExpression(axisName, axisValues.toList())
    }

    /**
     * Adds an axis that allows to run the same build on multiple nodes by evaluating a boolean expression..
     */
    void labelExpression(String axisName, Iterable<String> axisValues) {
        simpleAxis('LabelExp', axisName, axisValues)
    }

    /**
     * Adds a JDK axis.
     */
    void jdk(String... axisValues) {
        jdk(axisValues.toList())
    }

    /**
     * Adds a JDK axis.
     */
    void jdk(Iterable<String> axisValues) {
        simpleAxis('JDK', 'jdk', axisValues)
    }

    /**
     * Adds an axis that allows to build the project with multiple versions of Python.
     */
    @RequiresPlugin(id = 'shiningpanda', minimumVersion = '0.21')
    void python(String... axisValues) {
        python(axisValues.toList())
    }

    /**
     * Adds an axis that allows to build the project with multiple versions of Python.
     */
    @RequiresPlugin(id = 'shiningpanda', minimumVersion = '0.21')
    void python(Iterable<String> axisValues) {
        NodeBuilder nodeBuilder = new NodeBuilder()

        axisNodes << nodeBuilder.'jenkins.plugins.shiningpanda.matrix.PythonAxis' {
            name('PYTHON')
            values {
                axisValues.each { string(it) }
            }
        }
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code axes} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        configureBlocks << configureBlock
    }

    @Override
    protected void addExtensionNode(Node node) {
        axisNodes << node
    }

    private void simpleAxis(String axisType, String axisName, Iterable<String> axisValues) {
        NodeBuilder nodeBuilder = new NodeBuilder()

        axisNodes << nodeBuilder."hudson.matrix.${axisType}Axis" {
            name axisName
            values {
                axisValues.each { string it }
            }
        }
    }
}
