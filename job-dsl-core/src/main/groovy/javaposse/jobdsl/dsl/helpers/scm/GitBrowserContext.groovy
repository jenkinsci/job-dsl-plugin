package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class GitBrowserContext implements Context {
    Node browser

    void stash(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Stash') {
            delegate.url(url)
        }
    }

    /**
     * @since 1.35
     */
    void gitblit(String url, String projectName) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitBlitRepositoryBrowser') {
            delegate.url(url)
            delegate.projectName(projectName)
        }
    }

    /**
     * @since 1.35
     */
    void gitLab(String url, String version) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitLab') {
            delegate.url(url)
            delegate.version(version)
        }
    }
}
