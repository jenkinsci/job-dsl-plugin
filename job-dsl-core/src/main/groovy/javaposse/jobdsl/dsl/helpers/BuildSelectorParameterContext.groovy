package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.CopyArtifactSelectorContext

class BuildSelectorParameterContext extends AbstractContext {
    String description
    CopyArtifactSelectorContext defaultSelectorContext

    protected BuildSelectorParameterContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        defaultSelectorContext = new CopyArtifactSelectorContext(jobManagement, item, 'defaultSelector')
    }

    /**
     * Specifies the parameter description.
     *
     * @param description String value of the description
     */
    void description(String description) {
        this.description = description
    }

    /**
     * Specifies the default build selector.
     */
    void defaultBuildSelector(@DslContext(CopyArtifactSelectorContext) Closure defaultSelectorClosure) {
        ContextHelper.executeInContext(defaultSelectorClosure, defaultSelectorContext)
    }
}
