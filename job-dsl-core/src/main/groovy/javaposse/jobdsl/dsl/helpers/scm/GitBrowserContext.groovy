package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('hudson.scm.RepositoryBrowser')
class GitBrowserContext extends AbstractExtensibleContext {
    Node browser

    protected GitBrowserContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Use Stash as repository browser.
     */
    void stash(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Stash') {
            delegate.url(url)
        }
    }

    /**
     * Use Gitblit as repository browser.
     *
     * @since 1.35
     */
    void gitblit(String url, String projectName) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitBlitRepositoryBrowser') {
            delegate.url(url)
            delegate.projectName(projectName)
        }
    }

    /**
     * Use GitLab as repository browser.
     *
     * @since 1.35
     */
    void gitLab(String url, String version) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitLab') {
            delegate.url(url)
            delegate.version(version)
        }
    }

    /**
     * Use Gitiles as repository browser.
     *
     * @since 1.44
     */
    void gitiles(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Gitiles') {
            delegate.url(url)
        }
    }

    /**
     * Use GitWeb as repository browser.
     *
     * @since 1.44
     */
    void gitWeb(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitWeb') {
            delegate.url(url)
        }
    }

    /**
     * Use Gogs as repository browser.
     *
     * @since 1.64
     */
    void gogs(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GogsGit') {
            delegate.url(url)
        }
    }

    @Override
    protected void addExtensionNode(Node node) {
        browser = ContextHelper.toNamedNode('browser', node)
    }
}
