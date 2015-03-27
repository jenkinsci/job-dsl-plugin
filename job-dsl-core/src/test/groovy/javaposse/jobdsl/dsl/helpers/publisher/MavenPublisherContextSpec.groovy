package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class MavenPublisherContextSpec extends Specification {
    JobManagement jobManagement = Mock(JobManagement)
    MavenPublisherContext context = new MavenPublisherContext(jobManagement)

    def 'call deployArtifacts with no options'() {
        when:
        context.deployArtifacts()

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.maven.RedeployPublisher'
            children().size() == 3
            id[0].value().empty
            uniqueVersion[0].value() == true
            evenIfUnstable[0].value() == false
        }
    }

    def 'call deployArtifacts with all options'() {
        when:
        context.deployArtifacts {
            uniqueVersion(false)
            evenIfUnstable()
        }

        then:
        with(context.publisherNodes[0]) {
            name() == 'hudson.maven.RedeployPublisher'
            children().size() == 3
            id[0].value().empty
            uniqueVersion[0].value() == false
            evenIfUnstable[0].value() == true
        }
    }
}
