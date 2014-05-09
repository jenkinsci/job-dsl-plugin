package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.helpers.Context

class AxisContext implements Context {
    List<Node> axesNode = []

    /**
     * <hudson.matrix.TextAxis>
     * <name>rsatrs</name>
     * <values>
     * <string>a</string>
     * <string>b</string>
     * <string>c</string>
     * </values>
     * </hudson.matrix.TextAxis>
     */
    def textAxis(String name, List<String> values) {
        axis('Text', name, values)
    }

    /**
     * <hudson.matrix.LabelAxis>
     * <name>rsatrs</name>
     * <values>
     * <string>a</string>
     * <string>b</string>
     * <string>c</string>
     * </values>
     * </hudson.matrix.LabelAxis>
     */
    def labelAxis(String name, List<String> labels) {
        axis('Label', name, labels)
    }

    /**
     * <hudson.matrix.LabelExpAxis>
     * <name>rsatrs</name>
     * <values>
     * <string>a</string>
     * <string>b</string>
     * <string>c</string>
     * </values>
     * </hudson.matrix.LabelExpAxis>
     */
    def labelExpressionAxis(String name, List<String> expressions) {
        axis('LabelExp', name, expressions)
    }

    /**
     * <hudson.matrix.JDKAxis>
     * <name>rsatrs</name>
     * <values>
     * <string>a</string>
     * <string>b</string>
     * <string>c</string>
     * </values>
     * </hudson.matrix.JDKAxis>
     */
    def jdkAxis(String name, List<String> jdks) {
        axis('JDK', name, jdks)
    }

    private def axis(String type, String name, List<String> values) {
        def nodeBuilder = new NodeBuilder()
        Node axisNode = nodeBuilder."hudson.matrix.${type}Axis" {
            delegate.name(name)
            delegate.values {
                values.each { value ->
                    string(value)
                }
            }
        }
        axesNode << axisNode
    }
}