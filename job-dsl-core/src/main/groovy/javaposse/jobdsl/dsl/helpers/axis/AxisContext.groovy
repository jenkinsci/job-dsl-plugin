package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.helpers.Context

class AxisContext implements Context {
    List<Node> axisNodes = []

    AxisContext() {}

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

    def text(String axisName, Iterable<String> axisValues){
        simpleAxis( 'Text', axisName, axisValues)
    }

    def label(String axisName, Iterable<String> axisValues){
        simpleAxis( 'Label', axisName, axisValues)
    }

    def labelExpression(String axisName, Iterable<String> axisValues){
        simpleAxis( 'LabelExp', axisName, axisValues)
    }

    private enum AxisType {
        Text('hudson.matrix.TextAxis'),
        Label('hudson.matrix.LabelAxis'),
        LabelExp('hudson.matrix.LabelExpAxis')

        String axisName

        public AxisType(String elementName) {
            this.axisName = elementName
        }
    }

    private simpleAxis( String axis, String n, Iterable<String> v) {
        AxisType a = axis as AxisType
        def an = a.axisName
        def nodeBuilder = new NodeBuilder()

        axisNodes <<  nodeBuilder. "${an}" {
            name n
            values {
                v.each(){ string it}
            }
        }
    }


}
