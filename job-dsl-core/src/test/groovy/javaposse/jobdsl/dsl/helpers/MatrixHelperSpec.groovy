package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

class MatrixHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    MatrixHelper helper = new MatrixHelper(mockActions, JobType.MatrixJob)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.XML))

    def 'can set combinationFilter'() {
        when:
        helper.combinationFilter('LABEL1 == "TEST"')

        then:
        1 * mockActions.add(_)
    }

    def 'cannot set combinationFilter twice'() {
        when:
        helper.combinationFilter('LABEL1 == "TEST"')
        helper.combinationFilter('LABEL1 == "TEST"')

        then:
        thrown(IllegalStateException)
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

    def 'can set sequential'() {
        when:
        helper.sequential(false)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot set sequential twice'() {
        when:
        helper.sequential(true)
        helper.sequential(true)

        then:
        thrown(IllegalStateException)
    }

    def 'cannot set sequential for free style jobs'() {
        setup:
        MatrixHelper helper = new MatrixHelper(mockActions, JobType.Freeform)

        when:
        helper.sequential(true)

        then:
        thrown(IllegalStateException)
    }

    def 'sequential constructs xml'() {
        when:
        def action = helper.sequential(false)
        action.execute(root)

        then:
        root.executionStrategy.runSequentially.size() == 1
        root.executionStrategy.runSequentially[0].value() == 'false'
    }

    def 'can set touchStoneFilter'() {
        when:
        helper.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        1 * mockActions.add(_)
    }

    def 'cannot set touchStoneFilter twice'() {
        when:
        helper.touchStoneFilter('LABEL1 == "TEST"', true)
        helper.touchStoneFilter('LABEL1 == "TEST"', true)

        then:
        thrown(IllegalStateException)
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
        def action = helper.touchStoneFilter( 'LABEL1 == "TEST"', true )
        action.execute(root)

        then:
        root.executionStrategy.touchStoneCombinationFilter.size() == 1
        root.executionStrategy.touchStoneCombinationFilter[0].value() == 'LABEL1 == "TEST"'

        root.executionStrategy.touchStoneResultCondition.size() == 1
        //would like to check elements are [name,color,ordinal] here...
        root.executionStrategy.touchStoneResultCondition[0].value().each { it in [ 'UNSTABLE', 'YELLOW', 1 ] }
    }

    def 'can set axis'() {
        when:
        helper.axis { label( 'LABEL1', [ 'a', 'b', 'c'] ) }

        then:
        1 * mockActions.add(_)
    }
    def 'can set axis twice'() {
        when:
        helper.axis { label( 'LABEL1', [ 'a', 'b', 'c' ] ) }
        helper.axis { label( 'LABEL2', [ 'x', 'y', 'z' ] ) }

        then:
        2 * mockActions.add(_)
    }

    def 'cannot run axis for free style jobs'() {
        setup:
        MatrixHelper helper = new MatrixHelper(mockActions, JobType.Freeform)

        when:
        helper.axis { label( 'LABEL1', [ 'a', 'b', 'c' ] ) }

        then:
        thrown(IllegalStateException)
    }
}

