package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.helpers.Context

class AxisContext implements Context {
    List<Node> axisNodes = []
    /*
    * <project>
    *     <axes>
    *       <hudson.matrix.LabelAxis>
    *           <name>label</name>
    *           <values>
    *               <string>linux</string>
    *               <string>mac</string>
    *               <string>lamp</string>
    *               <string>master</string>
    *           </values>
    *       </hudson.matrix.LabelAxis>
    *       <hudson.matrix.LabelExpAxis>
    *           <name>label_exp</name>
    *           <values>
    *               <string>linux</string>
    *               <string>mac</string>
    *           </values>
    *       </hudson.matrix.LabelExpAxis>
    *       <hudson.matrix.TextAxis>
    *           <name>aaa</name>
    *           <values>
    *               <string>a</string>
    *               <string>b</string>
    *               <string>c</string>
    *           </values>
    *       </hudson.matrix.TextAxis>
    *     </axes>
    *  </project>
    * Provide axis for matrix (multi configuration job)
    */

    def text( String axisName, Iterable<String> axisValues) {
        simpleAxis( 'hudson.matrix.TextAxis', axisName, axisValues)
    }

    def label( String axisName, Iterable<String> axisValues) {
        simpleAxis( 'hudson.matrix.LabelAxis', axisName, axisValues)
    }

    def labelExpression( String axisName, Iterable<String> axisValues) {
        simpleAxis( 'hudson.matrix.LabelExpAxis', axisName, axisValues)
     }

    def jdk( String axisName, Iterable<String> axisValues) {
        simpleAxis( 'hudson.matrix.JDKAxis', axisName, axisValues)
    }

    private simpleAxis( String axis, String n, Iterable<String> v) {
        def nodeBuilder = new NodeBuilder()

        axisNodes <<  nodeBuilder. "${axis}" {
            name n
            values {
                v.each { string it }
            }
        }
    }
}
