package javaposse.jobdsl.dsl.helpers

import spock.lang.Specification

class AuthorizationHelperSpec extends Specification {
    AuthorizationContext context = new AuthorizationContext()

    def 'call permission methods'() {
        when:
        context.permission('hudson.model.Item.Configure:jill')

        then:
        context.permissions.size() == 1
        context.permissions[0] == 'hudson.model.Item.Configure:jill'

        when:
        context.permission(Permissions.ItemRead, 'jack')

        then:
        context.permissions.size() == 2
        context.permissions[1] == 'hudson.model.Item.Read:jack'

        when:
        context.permission('RunUpdate', 'joe')

        then:
        context.permissions.size() == 3
        context.permissions[2] == 'hudson.model.Run.Update:joe'
    }
}
