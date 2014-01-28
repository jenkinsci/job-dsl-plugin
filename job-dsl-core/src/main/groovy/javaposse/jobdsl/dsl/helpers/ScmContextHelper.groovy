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
 // TODO Pull all fields from GitSCM
 }

 subversion {
 String url
 // Strategy
 // TODO Pull all fields from SubversionSCM
 }
 */
class ScmContextHelper extends AbstractContextHelper<ScmContext> {
    private JobManagement jobManagement

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
            def scm = project/scm // TODO This will create it if it doesn't exist, seems like we wouldn't need to do this, but dealing with NodeList is a pain
            if (scm) {
                // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            // Assuming append the only child
            project << context.scmNode
        }
    }
}