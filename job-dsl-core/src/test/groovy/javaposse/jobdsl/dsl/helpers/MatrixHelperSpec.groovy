package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

class MatrixHelperSpec extends Specification {
    List<WithXmlAction> mockActions = Mock(List)
    MatrixHelper helper = new MatrixHelper(mockActions, JobType.Matrix)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'can set combinationFilter'() {
        when:
        helper.combinationFilter('LABEL1 == "TEST"')

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run combinationFilter for free style jobs'() {
        setup:
        MatrixHelper helper = new MatrixHelper(mockActions, JobType.Freeform)

        when:
        helper.combinationFilter('LABEL1 == "TEST"')

        then:
        thrown(IllegalStateException)
    }

    def 'combinationFilter constructs xml'() {
        when:
        def action = helper.combinationFilter('LABEL1 == "TEST"')
        action.execute(root)

        then:
        root.combinationFilter.size() == 1
        root.combinationFilter[0].value() == 'LABEL1 == "TEST"'
    }

    def 'can set runSequentially'() {
        when:
        helper.runSequentially(false)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot set runSequentially for free style jobs'() {
        setup:
        MatrixHelper helper = new MatrixHelper(mockActions, JobType.Freeform)

        when:
        helper.runSequentially(true)

        then:
        thrown(IllegalStateException)
    }

    def 'runSequentially constructs xml'() {
        when:
        def action = helper.runSequentially(false)
        action.execute(root)

        then:
        root.executionStrategy.runSequentially.size() == 1
        root.executionStrategy.runSequentially[0].value() == false
    }

    def 'can set touchStoneFilter'() {
        when:
        helper.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot set touchStoneFilter for free style jobs'() {
        setup:
        MatrixHelper helper = new MatrixHelper(mockActions, JobType.Freeform)

        when:
        helper.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        thrown(IllegalStateException)
    }

    def 'touchStoneFilter constructs xml'() {
        when:
        def action = helper.touchStoneFilter('LABEL1 == "TEST"', true)
        action.execute(root)

        then:
        with(root.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'UNSTABLE'
            touchStoneResultCondition[0].color[0].value() == 'YELLOW'
            touchStoneResultCondition[0].ordinal[0].value() == 1
        }

        when:
        action = helper.touchStoneFilter('LABEL1 == "TEST"', false)
        action.execute(root)

        then:
        with(root.executionStrategy) {
            touchStoneCombinationFilter.size() == 1
            touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'
            touchStoneResultCondition.size() == 1
            touchStoneResultCondition[0].children().size() == 3
            touchStoneResultCondition[0].name[0].value() == 'STABLE'
            touchStoneResultCondition[0].color[0].value() == 'BLUE'
            touchStoneResultCondition[0].ordinal[0].value() == 0
        }
    }

    def 'can set axis'() {
        when:
        helper.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        1 * mockActions.add(_)
    }

    def 'can set axis twice'() {
        when:
        helper.axes {
            label('LABEL1', 'a', 'b', 'c')
        }
        helper.axes {
            label('LABEL2', 'x', 'y', 'z')
        }

        then:
        2 * mockActions.add(_)
    }

    def 'cannot run axis for free style jobs'() {
        setup:
        MatrixHelper helper = new MatrixHelper(mockActions, JobType.Freeform)

        when:
        helper.axes {
            label('LABEL1', 'a', 'b', 'c')
        }

        then:
        thrown(IllegalStateException)
    }

    def 'axes configure block constructs xml'() {
        setup:
        List<WithXmlAction> actions = []
        MatrixHelper helper = new MatrixHelper(actions, JobType.Matrix)

        when:
        helper.axes {
            configure { axes ->
                axes << 'FooAxis'()
            }
        }

        then:
        actions.size() == 1

        when:
        actions[0].execute(root)

        then:
        root.axes.size() == 1
        root.axes[0].children().size() == 1
        root.axes[0].children()[0].name() == 'FooAxis'
    }
}

