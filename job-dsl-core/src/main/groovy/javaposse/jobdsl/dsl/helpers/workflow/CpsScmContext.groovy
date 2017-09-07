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
    Boolean lightweight = false
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

    /**
     * If selected, try to obtain the Pipeline script contents directly from the SCM without performing a full checkout.
     * The advantage of this mode is its efficiency; however,
     * you will not get any changelogs or polling based on the SCM.
     * (If you use checkout scm during the build, this will populate the changelog and initialize polling.)
     * Also build parameters will not be substituted into SCM configuration in this mode.
     * Only selected SCM plugins support this mode.
     * Defaults to
     * {@code false}.
     */
    void lightweight(Boolean lightweight) {
        this.lightweight = lightweight
    }
}
