package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement

class FreeStyleJob extends Job {
    static final String TEMPLATE = '''
        <?xml version='1.0' encoding='UTF-8'?>
        <project>
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
        </project>
    '''.stripIndent().trim()

    final String template = TEMPLATE

    FreeStyleJob(JobManagement jobManagement) {
        super(jobManagement)
    }
}
