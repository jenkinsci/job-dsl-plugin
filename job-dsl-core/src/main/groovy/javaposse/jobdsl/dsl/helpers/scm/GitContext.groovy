package javaposse.jobdsl.dsl.helpers.scm

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class GitContext implements Context {
    private final List<WithXmlAction> withXmlActions
    private final JobManagement jobManagement

    List<Node> remoteConfigs = []
    List<String> branches = []
    boolean createTag = false
    boolean clean = false
    boolean wipeOutWorkspace = false
    boolean remotePoll = false
    boolean shallowClone = false
    boolean pruneBranches = false
    String localBranch
    String relativeTargetDir
    String reference
    Closure withXmlClosure
    Node browser
    Node mergeOptions
    List<Node> extensions = []

    GitContext(List<WithXmlAction> withXmlActions, JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.withXmlActions = withXmlActions
    }

    void remote(Closure remoteClosure) {
        RemoteContext remoteContext = new RemoteContext(withXmlActions)
        executeInContext(remoteClosure, remoteContext)

        remoteConfigs << NodeBuilder.newInstance().'hudson.plugins.git.UserRemoteConfig' {
            if (remoteContext.name) {
                name(remoteContext.name)
            }
            if (remoteContext.refspec) {
                refspec(remoteContext.refspec)
            }
            url(remoteContext.url)
            if (remoteContext.credentials) {
                credentialsId(jobManagement.getCredentialsId(remoteContext.credentials))
            }
        }

        if (remoteContext.browser) {
            this.browser = remoteContext.browser
        }
    }

    void mergeOptions(String remote = null, String branch) {
        if (jobManagement.getPluginVersion('git-plugin')?.isOlderThan(new VersionNumber('2.0.0'))) {
            mergeOptions = NodeBuilder.newInstance().'userMergeOptions' {
                mergeRemote(remote ?: '')
                mergeTarget(branch)
            }
        } else {
            extensions << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.PreBuildMerge' {
                options {
                    mergeRemote(remote ?: '')
                    mergeTarget(branch)
                    mergeStrategy('default')
                }
            }
        }
    }

    void branch(String branch) {
        this.branches.add(branch)
    }

    void branches(String... branches) {
        this.branches.addAll(branches)
    }

    void createTag(boolean createTag = true) {
        this.createTag = createTag
    }

    void clean(boolean clean = true) {
        this.clean = clean
    }

    void wipeOutWorkspace(boolean wipeOutWorkspace = true) {
        this.wipeOutWorkspace = wipeOutWorkspace
    }

    void remotePoll(boolean remotePoll = true) {
        this.remotePoll = remotePoll
    }

    void shallowClone(boolean shallowClone = true) {
        this.shallowClone = shallowClone
    }

    void pruneBranches(boolean pruneBranches = true) {
        this.pruneBranches = pruneBranches
    }

    void localBranch(String localBranch) {
        this.localBranch = localBranch
    }

    void relativeTargetDir(String relativeTargetDir) {
        this.relativeTargetDir = relativeTargetDir
    }

    void reference(String reference) {
        this.reference = reference
    }

    void configure(Closure withXmlClosure) {
        this.withXmlClosure = withXmlClosure
    }

    void stashBrowser(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Stash') {
            delegate.url(url)
        }
    }
}
