package javaposse.jobdsl.dsl.helpers.workflow

import spock.lang.Specification

class BranchSourcesContextsSpec extends Specification {
    BranchSourcesContext context = new BranchSourcesContext()

    def 'git with minimal options'() {
        when:
        context.git {}

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 1
            with(source[0]) {
                children().size() == 6
                id[0].value() instanceof UUID
                remote[0].value().empty
                credentialsId[0].value().empty
                includes[0].value() == '*'
                excludes[0].value().empty
                ignoreOnPushNotifications[0].value() == false
            }
        }
    }

    def 'git with all options'() {
        when:
        context.git {
            remote('foo')
            credentialsId('bar')
            includes('lorem')
            excludes('ipsum')
            ignoreOnPushNotifications()
        }

        then:
        context.branchSourceNodes.size() == 1
        with(context.branchSourceNodes[0]) {
            name() == 'jenkins.branch.BranchSource'
            children().size() == 1
            with(source[0]) {
                children().size() == 6
                id[0].value() instanceof UUID
                remote[0].value() == 'foo'
                credentialsId[0].value() == 'bar'
                includes[0].value() == 'lorem'
                excludes[0].value() == 'ipsum'
                ignoreOnPushNotifications[0].value() == true
            }
        }
    }
}
