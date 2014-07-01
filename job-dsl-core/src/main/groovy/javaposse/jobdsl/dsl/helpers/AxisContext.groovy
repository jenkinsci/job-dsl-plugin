package javaposse.jobdsl.dsl.helpers

class AxisContext implements Context {
    List<Node> axisNodes = []
    List<Closure> configureBlocks = []

    /**
     * <hudson.matrix.TextAxis>
     *     <name>aaa</name>
     *     <values>
     *         <string>a</string>
     *         <string>b</string>
     *         <string>c</string>
     *     </values>
     * </hudson.matrix.TextAxis>
     */
    def text(String axisName, String... axisValues) {
        text(axisName, axisValues.toList())
    }

    def text(String axisName, Iterable<String> axisValues) {
        simpleAxis('Text', axisName, axisValues)
    }

    /**
     * <hudson.matrix.LabelAxis>
     *     <name>label</name>
     *     <values>
     *         <string>linux</string>
     *         <string>mac</string>
     *         <string>master</string>
     *     </values>
     * </hudson.matrix.LabelAxis>
     */
    def label(String axisName, String... axisValues) {
        label(axisName, axisValues.toList())
    }

    def label(String axisName, Iterable<String> axisValues) {
        simpleAxis('Label', axisName, axisValues)
    }

    /**
     * <hudson.matrix.LabelExpAxis>
     *     <name>label_exp</name>
     *     <values>
     *         <string>linux</string>
     *         <string>mac</string>
     *     </values>
     * </hudson.matrix.LabelExpAxis>
     */
    def labelExpression(String axisName, String... axisValues) {
        labelExpression(axisName, axisValues.toList())
    }

    def labelExpression(String axisName, Iterable<String> axisValues) {
        simpleAxis('LabelExp', axisName, axisValues)
    }

    /**
     * <hudson.matrix.JDKAxis>
     *     <name>jdk</name>
     *     <values>
     *         <string>jdk-6</string>
     *         <string>jdk-7</string>
     *     </values>
     * </hudson.matrix.JDKAxis>
     */
    def jdk(String... axisValues) {
        jdk(axisValues.toList())
    }

    def jdk(Iterable<String> axisValues) {
        simpleAxis('JDK', 'jdk', axisValues)
    }

    def configure(Closure closure) {
        configureBlocks << closure
    }

    private simpleAxis(String axisType, String axisName, Iterable<String> axisValues) {
        def nodeBuilder = new NodeBuilder()

        axisNodes << nodeBuilder."hudson.matrix.${axisType}Axis" {
            name axisName
            values {
                axisValues.each { string it }
            }
        }
    }
}
