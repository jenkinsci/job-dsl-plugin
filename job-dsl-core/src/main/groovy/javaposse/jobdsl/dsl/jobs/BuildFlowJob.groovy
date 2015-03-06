package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction

class BuildFlowJob extends Job {
    BuildFlowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    void buildFlow(String buildFlowText) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / dsl(buildFlowText)
        }
    }
}
