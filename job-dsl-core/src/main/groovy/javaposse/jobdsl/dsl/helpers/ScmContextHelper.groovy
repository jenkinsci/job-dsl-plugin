package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

/**
 scm {
 git(String url, Closure configure) // ||
 perforce(Closure configure) // ||
 subverison(Closure configure) ||
 }

 git {
 String url
 String branch
 }

 subversion {
 String url
 // Strategy
 }
 */
class ScmContextHelper extends AbstractContextHelper<ScmContext> {
    private final JobManagement jobManagement

    ScmContextHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    /**
     * Public method available directly on job {}
     * @param closure
     * @return
     */
    def scm(Closure closure) {
        execute(closure, new ScmContext(false, withXmlActions, jobManagement))
    }

    Closure generateWithXmlClosure(ScmContext context) {
        return { Node project ->
            def scm = project/scm
            if (scm) {
                // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            // Assuming append the only child
            project << context.scmNode
        }
    }
}
