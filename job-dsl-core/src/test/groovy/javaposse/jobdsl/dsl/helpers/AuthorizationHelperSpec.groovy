package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.WithXmlActionSpec
import spock.lang.Specification

public class AuthorizationHelperSpec extends Specification {

    List<WithXmlAction> mockActions = Mock()
    AuthorizationContextHelper helper = new AuthorizationContextHelper(mockActions, JobType.Freeform)
    AuthorizationContext context = new AuthorizationContext()

    def 'call permission methods'() {
        when:
        context.permission('hudson.model.Item.Configure:jill')

        then:
        context.perms.size() == 1
        context.perms[0] == 'hudson.model.Item.Configure:jill'

        when:
        context.permission(Permissions.ItemRead, 'jack')

        then:
        context.perms.size() == 2
        context.perms[1] == 'hudson.model.Item.Read:jack'

        when:
        context.permission('RunUpdate', 'joe')

        then:
        context.perms.size() == 3
        context.perms[2] == 'hudson.model.Run.Update:joe'
    }

    def 'call authorization'() {
        when:
        def ret = helper.authorization {
            permission('hudson.model.Item.Configure:jill')
            permission('hudson.model.Item.Configure:jack')
        }

        then:
        ret.size() == 2
        1 * mockActions.add(_)

        when:
        helper.authorization.permission('hudson.model.Item.Configure:john')

        then:
        1 * mockActions.add(_)

        when:
        helper.authorization
                .permission('hudson.model.Item.Configure:john')
                .permission('hudson.model.Item.Configure:joe')

        then:
        2 * mockActions.add(_)
    }

    def 'execute withXml Action'() {
        Node root = new XmlParser().parse(new StringReader(WithXmlActionSpec.xml))
        List<String> perms = ['hudson.model.Item.Configure:jill', 'hudson.model.Item.Configure:jack']

        when:
        def withXmlAction = helper.generateWithXmlAction(new AuthorizationContext(perms))
        withXmlAction.execute(root)

        then:
        NodeList permissions = root.properties[0].'hudson.security.AuthorizationMatrixProperty'[0].permission
        permissions.size() == 2
        permissions[0].text() == 'hudson.model.Item.Configure:jill'
        permissions[1].text() == 'hudson.model.Item.Configure:jack'
    }
}
