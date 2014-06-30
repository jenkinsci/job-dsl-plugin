package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

class AxisContextSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    AxisContext context = new AxisContext()
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'can set label'() {
        when:
        context.label( 'LABEL1', [ 'a', 'b', 'c' ] )

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].each { it in [ 'a', 'b', 'c' ] }
    }
    def 'can set label twice'() {
        when:
        context.label( 'LABEL1', [ 'a', 'b', 'c' ] )
        context.label( 'LABEL2', [ 'x', 'y', 'z' ] )

        then:
        context.axisNodes.size() == 2
        context.axisNodes[0].each { it in [ 'a', 'b', 'c' ] }
        context.axisNodes[1].each { it in [ 'x', 'y', 'z' ] }
    }
    def 'can set text'() {
        when:
        context.text( 'LABEL1', [ 'a', 'b', 'c' ] )

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].each { it in [ 'a', 'b', 'c' ] }
    }
    def 'can set text twice'() {
        when:
        context.text('LABEL1', [ 'a', 'b', 'c' ] )
        context.text('LABEL2', [ 'x', 'y', 'z' ] )

        then:
        context.axisNodes.size() == 2
        context.axisNodes[0].each { it in [ 'a', 'b', 'c' ] }
        context.axisNodes[1].each { it in [ 'x', 'y', 'z' ] }
    }
    def 'can set labelExpression'() {
        when:
        context.labelExpression( 'LABEL1', [ 'a', 'b', 'c' ] )

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].each { it in [ 'a', 'b', 'c' ] }
    }
    def 'can set labelExpression twice'() {
        when:
        context.labelExpression( 'LABEL1', [ 'a', 'b', 'c' ] )
        context.labelExpression( 'LABEL2', [ 'x', 'y', 'z' ] )

        then:
        context.axisNodes.size() == 2
        context.axisNodes[0].each { it in [ 'a', 'b', 'c' ] }
        context.axisNodes[1].each { it in [ 'x', 'y', 'z' ] }
    }
}

