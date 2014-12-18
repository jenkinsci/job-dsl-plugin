package javaposse.jobdsl.dsl.helpers.axis

import javaposse.jobdsl.dsl.helpers.AxisContext
import spock.lang.Specification

class AxisContextSpec extends Specification {
    AxisContext context = new AxisContext()

    def 'can set label'() {
        when:
        context.label('LABEL1', 'a', 'b', 'c')

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].name() == 'hudson.matrix.LabelAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'LABEL1'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
    }

    def 'can set label twice'() {
        when:
        context.label('LABEL1', 'a', 'b', 'c')
        context.label('LABEL2', 'x', 'y', 'z')

        then:
        context.axisNodes.size() == 2
        context.axisNodes[0].name() == 'hudson.matrix.LabelAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'LABEL1'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
        context.axisNodes[1].name() == 'hudson.matrix.LabelAxis'
        context.axisNodes[1].children().size() == 2
        context.axisNodes[1].name[0].value() == 'LABEL2'
        context.axisNodes[1].values[0].children().size() == 3
        context.axisNodes[1].values[0].string[0].value() == 'x'
        context.axisNodes[1].values[0].string[1].value() == 'y'
        context.axisNodes[1].values[0].string[2].value() == 'z'
    }

    def 'can set text'() {
        when:
        context.text('LABEL1', 'a', 'b', 'c')

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].name() == 'hudson.matrix.TextAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'LABEL1'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
    }

    def 'can set text twice'() {
        when:
        context.text('LABEL1', 'a', 'b', 'c')
        context.text('LABEL2', 'x', 'y', 'z')

        then:
        context.axisNodes.size() == 2
        context.axisNodes[0].name() == 'hudson.matrix.TextAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'LABEL1'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
        context.axisNodes[1].name() == 'hudson.matrix.TextAxis'
        context.axisNodes[1].children().size() == 2
        context.axisNodes[1].name[0].value() == 'LABEL2'
        context.axisNodes[1].values[0].children().size() == 3
        context.axisNodes[1].values[0].string[0].value() == 'x'
        context.axisNodes[1].values[0].string[1].value() == 'y'
        context.axisNodes[1].values[0].string[2].value() == 'z'
    }

    def 'can set labelExpression'() {
        when:
        context.labelExpression('LABEL1', 'a', 'b', 'c')

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].name() == 'hudson.matrix.LabelExpAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'LABEL1'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
    }

    def 'can set labelExpression twice'() {
        when:
        context.labelExpression('LABEL1', 'a', 'b', 'c')
        context.labelExpression('LABEL2', 'x', 'y', 'z')

        then:
        context.axisNodes.size() == 2
        context.axisNodes[0].name() == 'hudson.matrix.LabelExpAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'LABEL1'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
        context.axisNodes[1].name() == 'hudson.matrix.LabelExpAxis'
        context.axisNodes[1].children().size() == 2
        context.axisNodes[1].name[0].value() == 'LABEL2'
        context.axisNodes[1].values[0].children().size() == 3
        context.axisNodes[1].values[0].string[0].value() == 'x'
        context.axisNodes[1].values[0].string[1].value() == 'y'
        context.axisNodes[1].values[0].string[2].value() == 'z'
    }

    def 'can set jdk'() {
        when:
        context.jdk('a', 'b', 'c')

        then:
        context.axisNodes.size() == 1
        context.axisNodes[0].name() == 'hudson.matrix.JDKAxis'
        context.axisNodes[0].children().size() == 2
        context.axisNodes[0].name[0].value() == 'jdk'
        context.axisNodes[0].values[0].children().size() == 3
        context.axisNodes[0].values[0].string[0].value() == 'a'
        context.axisNodes[0].values[0].string[1].value() == 'b'
        context.axisNodes[0].values[0].string[2].value() == 'c'
    }
}

