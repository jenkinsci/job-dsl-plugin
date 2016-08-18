package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.ScmContext

class CpsScmContext extends AbstractContext {
    protected final Item item

    String scriptPath = 'JenkinsFile'
    ScmContext scmContext = new ScmContext(jobManagement, item)

    CpsScmContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Specifies where to obtain a source code repository containing the pipeline script.
     */
    void scm(@DslContext(ScmContext) Closure scmClosure) {
        ContextHelper.executeInContext(scmClosure, scmContext)
    }

    /**
     * Sets the relative location of the pipeline script within the source code repository. Defaults to
     * {@code 'pipeline'}.
     */
    void scriptPath(String scriptPath) {
        this.scriptPath = scriptPath
    }
}
