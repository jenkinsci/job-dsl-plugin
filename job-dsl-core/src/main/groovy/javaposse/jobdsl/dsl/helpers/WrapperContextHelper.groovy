package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

import static javaposse.jobdsl.dsl.helpers.WrapperContext.Timeout.absolute

class WrapperContextHelper extends AbstractContextHelper<WrapperContext> {

    WrapperContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    def wrappers(Closure closure) {
        execute(closure, new WrapperContext(type))
    }

    def runOnSameNodeAs(String jobName, boolean useSameWorkspace = false) {
        wrappers {
            delegate.runOnSameNodeAs(jobName, useSameWorkspace)
        }
    }

    def rvm(String rubySpecification) {
        wrappers {
            delegate.rvm(rubySpecification)
        }
    }
    def timeout(Integer timeoutInMinutes, Boolean shouldFailBuild = true) {
        wrappers {
            delegate.timeout(timeoutInMinutes, shouldFailBuild)
        }
    }

    def timeout(String type = absolute.toString(), Closure timeoutClosure = null) {
        wrappers {
            delegate.timeout(type, timeoutClosure)
        }
    }

    def allocatePorts(String[] portsArg, Closure closure = null) {
        wrappers {
            delegate.allocatePorts(portsArg, closure)
        }
    }

    def allocatePorts(Closure cl = null) {
        wrappers {
            delegate.allocatePorts(cl)
        }
    }

    Closure generateWithXmlClosure(WrapperContext context) {
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
