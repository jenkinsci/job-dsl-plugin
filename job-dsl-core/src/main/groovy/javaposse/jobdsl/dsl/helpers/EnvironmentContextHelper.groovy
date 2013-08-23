package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import static javaposse.jobdsl.dsl.helpers.EnvironmentContext.Timeout.absolute

class EnvironmentContextHelper extends AbstractContextHelper<EnvironmentContext> {

    EnvironmentContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def environment(Closure closure) {
        execute(closure, new EnvironmentContext(type))
    }

    def runOnSameNodeAs(String jobName, boolean useSameWorkspace = false) {
        environment {
            delegate.runOnSameNodeAs(jobName, useSameWorkspace)
        }
    }

    def rvm(String rubySpecification) {
        environment {
            delegate.rvm(rubySpecification)
        }
    }
    def timeout(Integer timeoutInMinutes, Boolean shouldFailBuild = true) {
        environment {
            delegate.timeout(timeoutInMinutes, shouldFailBuild)
        }
    }

    def timeout(String type = absolute.toString(), Closure timeoutClosure = null) {
        environment {
            delegate.timeout(type, timeoutClosure)
        }
    }

    def allocatePorts(String[] portsArg, Closure closure = null) {
        environment {
            delegate.allocatePorts(portsArg, closure)
        }
    }

    def allocatePorts(Closure cl = null) {
        environment {
            delegate.allocatePorts(cl)
        }
    }

    Closure generateWithXmlClosure(EnvironmentContext context) {
        return { Node project ->
            def wrapperNode
            if (project.buildWrappers.isEmpty()) {
                wrapperNode = project.appendNode('buildWrappers')
            } else {
                wrapperNode = project.buildWrappers[0]
            }
            context.wrapperNodes.each {
                wrapperNode << it
            }
        }
    }
}
