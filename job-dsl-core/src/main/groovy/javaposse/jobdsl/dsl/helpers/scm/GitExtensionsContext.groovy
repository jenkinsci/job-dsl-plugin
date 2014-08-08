package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context

class GitExtensionsContext implements Context {
    private final List<WithXmlAction> withXmlActions

    Node localBranchExtension

    GitExtensionsContext(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }

    void localBranch(String localBranch) {
        localBranchExtension = NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.LocalBranch' {
            delegate.localBranch(localBranch)
        }
    }
}
