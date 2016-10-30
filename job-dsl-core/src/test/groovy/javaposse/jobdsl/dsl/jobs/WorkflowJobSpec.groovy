package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class WorkflowJobSpec extends Specification {
    private final JobManagement jobManagement = Mock(JobManagement)
    private final WorkflowJob job = new WorkflowJob(jobManagement, 'test')

    def 'construct simple workflow job and generate xml from it'() {
        when:
        def xml = job.node

        then:
        xml.name() == 'flow-definition'
        xml.children().size() == 6
    }

    def 'minimal cps workflow'() {
        when:
        job.definition {
            cps {
            }
        }

        then:
        with(job.node.definition[0]) {
            attribute('class') == 'org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition'
            children().size() == 2
            script[0].value() == ''
            sandbox[0].value() == false
        }
    }

    def 'full cps workflow'() {
        when:
        job.definition {
            cps {
                script('foo')
                sandbox()
            }
        }

        then:
        with(job.node.definition[0]) {
            attribute('class') == 'org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition'
            children().size() == 2
            script[0].value() == 'foo'
            sandbox[0].value() == true
        }
    }

    def 'minimal cps scm workflow'() {
        when:
        job.definition {
            cpsScm {
                scm {
                    git('https://github.com/jenkinsci/job-dsl-plugin.git')
                }
            }
        }

        then:
        with(job.node.definition[0]) {
            attribute('class') == 'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition'
            children().size() == 2
            scm[0].attribute('class') == 'hudson.plugins.git.GitSCM'
            scm[0].children().size() == 6
            scriptPath[0].value() == 'JenkinsFile'
        }
        1 * jobManagement.requireMinimumPluginVersion('workflow-cps', '1.2')
    }

    def 'full cps scm workflow'() {
        when:
        job.definition {
            cpsScm {
                scm {
                    git('https://github.com/jenkinsci/job-dsl-plugin.git')
                }
                scriptPath('.jenkins/Jenkinsfile')
            }
        }

        then:
        with(job.node.definition[0]) {
            attribute('class') == 'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition'
            children().size() == 2
            scm[0].attribute('class') == 'hudson.plugins.git.GitSCM'
            scm[0].children().size() == 6
            scriptPath[0].value() == '.jenkins/Jenkinsfile'
        }
        1 * jobManagement.requireMinimumPluginVersion('workflow-cps', '1.2')
    }

    def 'cps scm workflow without scm'() {
        when:
        job.definition {
            cpsScm {
                scriptPath('.jenkins/Jenkinsfile')
            }
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'SCM must be specified'
    }

    def 'cps scm workflow with multiple SCMs'() {
        when:
        job.definition {
            cpsScm {
                scm {
                    github('foo/bar')
                    github('foo/baz')
                }
                scriptPath('.jenkins/Jenkinsfile')
            }
        }

        then:
        Exception e = thrown(DslScriptException)
        e.message =~ 'only one SCM can be specified'
    }
}
