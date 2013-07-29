package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification

class WrapperHelperSpec extends Specification {
    List<WithXmlAction> mockActions = Mock()
    WrapperContextHelper helper = new WrapperContextHelper(mockActions, JobType.Freeform)
    WrapperContext context = new WrapperContext(JobType.Freeform)

    def 'call timestamps method'() {
        when:
        context.timestamps()

        then:
        context.wrapperNodes?.size() == 1

        def timestampWrapper = context.wrapperNodes[0]
        timestampWrapper.name() == 'hudson.plugins.timestamper.TimestamperBuildWrapper'
    }
}
