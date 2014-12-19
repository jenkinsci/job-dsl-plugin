package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class GitBrowserContext implements Context {
    Node browser

    /**
     * <hudson.plugins.git.browser.Stash>
     *     <url>http://acme.org/repo</url>
     * </hudson.plugins.git.browser.Stash>
     */
    void stash(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Stash') {
            delegate.url(url)
        }
    }
}
