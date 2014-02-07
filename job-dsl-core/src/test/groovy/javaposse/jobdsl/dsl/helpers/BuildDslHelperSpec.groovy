package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import javaposse.jobdsl.dsl.helpers.common.BuildDslContext
import spock.lang.Specification

public class BuildDslHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    BuildDslHelper helper = new BuildDslHelper(mockActions, JobType.BuildDsl)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))

    def 'can run buildDslBlock'() {
        when:
        helper.buildDslBlock("build block")

        then:
        1 * mockActions.add(_)
    }

    def 'cannot run buildDslBlock twice'() {
        when:
        helper.buildDslBlock("build block")
        helper.buildDslBlock("build block again")

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run buildDsl for free style jobs'() {
        setup:
        BuildDslHelper helper = new BuildDslHelper(mockActions, JobType.Freeform)

        when:
        helper.buildDslBlock("build block")

        then:
        thrown(IllegalStateException)
    }

    def 'cannot run buildDsl for Maven jobs'() {
        setup:
        BuildDslHelper helper = new BuildDslHelper(mockActions, JobType.Maven)

        when:
        helper.buildDslBlock("build block")

        then:
        thrown(IllegalStateException)
    }


    def 'buildDslBlock constructs xml'() {
        when:
        def action = helper.buildDslBlock("build Dsl Block")
        action.execute(root)

        then:
        root.dsl.size() == 1
        root.dsl[0].value() == "build Dsl Block"
    }

}
