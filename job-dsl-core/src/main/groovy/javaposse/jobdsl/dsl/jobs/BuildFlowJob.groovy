package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction

class BuildFlowJob extends Job {

    static final String TEMPLATE = '''
        <?xml version='1.0' encoding='UTF-8'?>
        <com.cloudbees.plugins.flow.BuildFlow>
          <actions/>
          <description></description>
          <keepDependencies>false</keepDependencies>
          <properties/>
          <scm class="hudson.scm.NullSCM"/>
          <canRoam>true</canRoam>
          <disabled>false</disabled>
          <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
          <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
          <triggers class="vector"/>
          <concurrentBuild>false</concurrentBuild>
          <builders/>
          <publishers/>
          <buildWrappers/>
          <icon/>
          <dsl></dsl>
        </com.cloudbees.plugins.flow.BuildFlow>
    '''.stripIndent().trim()

    final String template = TEMPLATE

    BuildFlowJob(JobManagement jobManagement) {
        super(jobManagement)
    }

    void buildFlow(String buildFlowText) {
        withXmlActions << WithXmlAction.create { Node project ->
            project / dsl(buildFlowText)
        }
    }
}
