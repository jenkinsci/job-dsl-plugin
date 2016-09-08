package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class GitContext extends AbstractContext {
    private final Item item

    List<Node> remoteConfigs = []
    List<String> branches = []
    Closure configureBlock
    final GitBrowserContext gitBrowserContext = new GitBrowserContext(jobManagement)
    GitExtensionContext extensionContext = new GitExtensionContext(jobManagement, item)

    GitContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Adds a remote. Can be repeated to add multiple remotes.
     */
    void remote(@DslContext(RemoteContext) Closure remoteClosure) {
        RemoteContext remoteContext = new RemoteContext(item)
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
                credentialsId(remoteContext.credentials)
            }
        }

        if (remoteContext.browser) {
            gitBrowserContext.browser = remoteContext.browser
        }
    }

    /**
     * Specify the branches to examine for changes and to build.
     */
    void branch(String branch) {
        this.branches.add(branch)
    }

    /**
     * Specify the branches to examine for changes and to build.
     */
    void branches(String... branches) {
        this.branches.addAll(branches)
    }

    /**
     * Adds a repository browser for browsing the details of changes in an external system.
     *
     * @since 1.26
     */
    void browser(@DslContext(GitBrowserContext) Closure gitBrowserClosure) {
        executeInContext(gitBrowserClosure, gitBrowserContext)
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code scm} node is passed into the configure block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }

    /**
     * Adds additional behaviors.
     *
     * @since 1.44
     */
    void extensions(@DslContext(GitExtensionContext) Closure closure) {
        executeInContext(closure, extensionContext)
    }
}
