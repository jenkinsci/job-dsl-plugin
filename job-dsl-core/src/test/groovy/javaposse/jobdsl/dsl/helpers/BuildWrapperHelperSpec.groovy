package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import spock.lang.Specification

class BuildWrapperHelperSpec extends Specification {
    List<WithXmlAction> mockActions = Mock()
    BuildWrapperContextHelper helper = new BuildWrapperContextHelper(mockActions, JobType.Freeform)
    BuildWrapperContext context = new BuildWrapperContext(JobType.Freeform)

    def 'call timestamps method'() {
        when:
        context.timestamps()

        then:
        context.buildWrapperNodes?.size() == 1

        def timestampWrapper = context.buildWrapperNodes[0]
        timestampWrapper.name() == 'hudson.plugins.timestamper.TimestamperBuildWrapper'
    }
}
