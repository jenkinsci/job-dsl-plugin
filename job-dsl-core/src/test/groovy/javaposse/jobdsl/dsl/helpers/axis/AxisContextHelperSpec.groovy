package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification
import spock.lang.Unroll

class AxisContextHelperSpec extends Specification {
    List<WithXmlAction> mockActions = Mock()
    AxisContextHelper helper = new AxisContextHelper(mockActions, JobType.Freeform)
    AxisContext context = new AxisContext()

    @Unroll
    def 'call #type axis'(String method, String type) {
        when:
        context."${method}"('myText', ['value1', 'value2', 'value3'])

        then:
        context.axesNode.size() == 1
        context.axesNode[0].name() == type
        context.axesNode[0].with {
            name[0].value() == 'myText'
            values[0].string.size() == 3
            values[0].string*.value() == ['value1', 'value2', 'value3']
        }

        where:
        method               || type
        'textAxis'            | 'hudson.matrix.TextAxis'
        'labelAxis'           | 'hudson.matrix.LabelAxis'
        'labelExpressionAxis' | 'hudson.matrix.LabelExpAxis'
        'jdkAxis'             | 'hudson.matrix.JDKAxis'

    }
}
