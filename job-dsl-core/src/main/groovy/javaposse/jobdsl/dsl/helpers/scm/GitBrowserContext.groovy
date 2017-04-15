package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GitBrowserContext extends AbstractContext {
    Node browser

    protected GitBrowserContext(JobManagement jobManagement) {
        super(jobManagement)
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
}
