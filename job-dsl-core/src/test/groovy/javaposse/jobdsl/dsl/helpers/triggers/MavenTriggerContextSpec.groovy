package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MavenTriggerContextSpec extends Specification {
    JobManagement mockJobManagement = Mock(JobManagement)

    def 'call snapshotDependencies for Maven job succeeds'() {
        when:
        MavenTriggerContext context = new MavenTriggerContext([], mockJobManagement)
        context.snapshotDependencies(false)

        then:
        context.withXmlActions != null
        context.withXmlActions.size() == 1
    }
}
