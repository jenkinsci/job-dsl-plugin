package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

/**
 multiscm {
   git(String url, Closure configure) // &&
   perforce(Closure configure) // &&
   subverison(Closure configure)
 }
 */
class MultiScmContextHelper extends AbstractContextHelper<ScmContext> {
    private final JobManagement jobManagement

    MultiScmContextHelper(List<WithXmlAction> withXmlActions, JobType jobType, JobManagement jobManagement) {
        super(withXmlActions, jobType)
        this.jobManagement = jobManagement
    }

    /**
     * Public method available directly on job {}
     * @param closure
     * @return
     */
    def multiscm(Closure closure) {
        execute(closure, new ScmContext(true, withXmlActions, jobManagement))
    }

    /**
     <scm class="org.jenkinsci.plugins.multiplescms.MultiSCM" plugin="multiple-scms@0.2">
     <scms>
     ...
     </scms>
     </scm>
     * @param configure
     */

    Closure generateWithXmlClosure(ScmContext context) {
        return { Node project ->
            def scm = project / scm

            if (scm) {
                    // There can only be only one SCM, so remove if there
                project.remove(scm)
            }

            def nodeBuilder = new NodeBuilder()
            def multiscmNode = nodeBuilder.scm(class: 'org.jenkinsci.plugins.multiplescms.MultiSCM')
            def scmsNode = multiscmNode / scms
            context.scmNodes.each {
                scmsNode << it
            }

                // Assuming append the only child
            project << multiscmNode
        }
    }
}
