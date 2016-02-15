package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class P4WorkspaceContext implements Context {
    Node workspaceConfig

    P4WorkspaceContext() {
        manual('', '')
    }

    /**
     * Set up a manual workspace with workspace name and view spec.
     */
    void manual(String workspaceName, String viewSpec) {
        workspaceConfig = new NodeBuilder().workspace(class: 'org.jenkinsci.plugins.p4.workspace.ManualWorkspaceImpl') {
            charset('none')
            pinHost(false)
            name(workspaceName ?: '')
            spec {
                allwrite(false)
                clobber(false)
                compress(false)
                locked(false)
                modtime(false)
                rmdir(false)
                streamName()
                line('LOCAL')
                view(viewSpec ?: '')
            }
        }
    }
}
