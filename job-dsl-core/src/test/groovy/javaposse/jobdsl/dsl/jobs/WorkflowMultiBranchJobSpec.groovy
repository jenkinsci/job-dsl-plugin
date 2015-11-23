package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class WorkflowMultiBranchJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final WorkflowMultiBranchJob job = new WorkflowMultiBranchJob(jobManagement)

    def 'construct simple workflow multi branch job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject'
        xml.children().size() == 12
    }

    def 'can add branchSource'() {
        when:
        job.branchSource {
            git {
                remote('')
                credentialsId('')
                includes('')
                excludes('')
                ignoreOnPushNotifications(false)
            }
        }

        then:
        job.node.sources[0].data.size() == 1
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'[0].children().size() == 2
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'[0]
                .'source'[0].attribute('class') == 'jenkins.plugins.git.GitSCMSource'
    }

    def 'can add orphanedItemStrategy'() {
        when:
        job.orphanedItemStrategy {
            pruneDeadBranches(true)
            daysToKeep(0)
            numToKeep(0)
        }

        then:
        job.node.orphanedItemStrategy[0]
                .attribute('class') == 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
        job.node.orphanedItemStrategy[0].children().size() == 3
    }
}
