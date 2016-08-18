package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class P4Context implements Context {
    P4WorkspaceContext workspaceContext = new P4WorkspaceContext()
    Closure configureBlock

    /**
     * Sets the appropriate Perforce workspace behaviour.
     */
    void workspace(@DslContext(P4WorkspaceContext) Closure closure) {
        ContextHelper.executeInContext(closure, workspaceContext)
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code scm} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
}
