package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.PromotionContext
import spock.lang.Specification

class PromotionSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)

    def 'construct simple Promotion and generate xml from it without configure closure'() {
        setup:
        PromotionContext context = new PromotionContext(jobManagement, 'TestName')
        Promotion promotion = new Promotion(jobManagement, context)

        when:
        def xml = promotion.node

        then:
        xml
        xml.name() == 'hudson.plugins.promoted__builds.PromotionProcess'
        xml.children().size() == 13
    }

    def 'construct simple Promotion and generate xml from it with configure closure'() {
        setup:
        PromotionContext context = new PromotionContext(jobManagement, 'TestName')
        context.configureClosure = { root ->
            root / 'newNode' / 'newThing' {
                myNode 'myVal'
            }
        }
        Promotion promotion = new Promotion(jobManagement, context)

        when:
        def xml = promotion.node

        then:
        xml
        xml.name() == 'hudson.plugins.promoted__builds.PromotionProcess'
        xml.children().size() == 14
    }
}
