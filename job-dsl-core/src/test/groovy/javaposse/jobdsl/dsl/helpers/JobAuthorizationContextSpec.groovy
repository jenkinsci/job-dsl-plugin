package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class JobAuthorizationContextSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final JobAuthorizationContext context = new JobAuthorizationContext(jobManagement)

    def 'call permission methods'() {
        setup:
        jobManagement.getPermissions('hudson.security.AuthorizationMatrixProperty') >> [
                'hudson.model.Item.Configure',
                'hudson.model.Item.Read',
                'hudson.model.Run.Update'
        ]

        when:
        context.permission('hudson.model.Item.Configure:jill')

        then:
        context.permissions.size() == 1
        context.permissions.contains('hudson.model.Item.Configure:jill')

        when:
        context.permission('hudson.model.Run.Update', 'john')

        then:
        context.permissions.size() == 2
        context.permissions.contains('hudson.model.Run.Update:john')
    }

    def 'call permissionAll method'() {
        setup:
        jobManagement.getPermissions('hudson.security.AuthorizationMatrixProperty') >> [
                'hudson.model.Item.Configure',
                'hudson.model.Item.Read',
                'hudson.model.Run.Update'
        ]

        when:
        context.permissionAll('jill')

        then:
        context.permissions.size() == 3
        context.permissions.contains('hudson.model.Item.Configure:jill')
        context.permissions.contains('hudson.model.Item.Read:jill')
        context.permissions.contains('hudson.model.Run.Update:jill')
    }

    def 'call permissions method'() {
        setup:
        jobManagement.getPermissions('hudson.security.AuthorizationMatrixProperty') >> [
                'hudson.model.Item.Configure',
                'hudson.model.Item.Read',
                'hudson.model.Run.Update'
        ]

        when:
        context.permissions('jill', [
          'hudson.model.Item.Configure',
          'hudson.model.Item.Read'
        ])

        then:
        context.permissions.size() == 2
        context.permissions.contains('hudson.model.Item.Configure:jill')
        context.permissions.contains('hudson.model.Item.Read:jill')
    }
}
