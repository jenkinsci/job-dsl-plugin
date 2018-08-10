package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.workflow.BranchProjectFactoryContext
import spock.lang.Specification

class MultibranchWorkflowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final MultibranchWorkflowJob job = new MultibranchWorkflowJob(jobManagement, 'test')

    def 'construct simple workflow multi branch job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject'
        xml.children().size() == 12
    }

    def 'can add branchSource'() {
        when:
        job.branchSources {
            git {}
            svn {}
        }

        then:
        job.node.sources[0].data.size() == 1
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'.size() == 2
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'[0].children().size() == 2
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'[0]
                .'source'[0].attribute('class') == 'jenkins.plugins.git.GitSCMSource'
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'[1].children().size() == 2
        job.node.sources[0].data[0].'jenkins.branch.BranchSource'[1]
                .'source'[0].attribute('class') == 'jenkins.scm.impl.subversion.SubversionSCMSource'
    }

    def 'can add orphanedItemStrategy'() {
        when:
        job.orphanedItemStrategy {
            discardOldItems {
                daysToKeep(20)
            }
        }

        then:
        job.node.orphanedItemStrategy[0]
                .attribute('class') == 'com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy'
        job.node.orphanedItemStrategy[0].children().size() == 3
        job.node.orphanedItemStrategy[0].daysToKeep[0].value() == 20
    }

    def 'call triggers'() {
        when:
        job.triggers {
            periodic(2)
        }

        then:
        with(job.node) {
            triggers[0].'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'[0].interval[0].text() ==
                    '120000'
        }
    }

    def 'can set the factory'() {
        setup:
        jobManagement.callExtension('factory1', job, BranchProjectFactoryContext, []) >>
                new Node(null, 'org.example.BranchProjectFactory1')

        when:
        job.factory {
            factory1()
        }

        then:
        with(job.node) {
            factory.size() == 1
            with(factory[0]) {
                children().size() == 0
                attribute('class') == 'org.example.BranchProjectFactory1'
            }
        }
    }
}
