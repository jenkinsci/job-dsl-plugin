package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction;
import javaposse.jobdsl.dsl.WithXmlActionSpec;
import spock.lang.Specification

class ItemHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    ItemHelper helper = new ItemHelper(mockActions)
    Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))

    def 'add description'() {
        when:
        def action = helper.description('Description')
        action.execute(root)

        then:
        root.description[0].value() == 'Description'

        when:
        def action2 = helper.description('Description2')
        action2.execute(root)

        then:
        root.description.size() == 1
        root.description[0].value() == 'Description2'

    }

    def 'add display name' () {
        when:
        def action = helper.displayName('FooBar')
        action.execute(root)

        then:
        root.displayName[0].value() == 'FooBar'
    }

}
