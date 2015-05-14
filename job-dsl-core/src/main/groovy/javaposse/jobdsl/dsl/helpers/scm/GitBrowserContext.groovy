package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class GitBrowserContext implements Context {
    Node browser

    void stash(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Stash') {
            delegate.url(url)
        }
    }

    void assemblaweb(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.AssemblaWeb') {
            delegate.repoUrl(repoUrl)
        }
    }

    void fisheye(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.FisheyeGitRepositoryBrowser') {
            delegate.url(url)
        }
    }

    void kiln(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.KilnGit') {
            delegate.url(url)
        }
    }

    void tfs2013(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.TFS2013GitRepositoryBrowser') {
            delegate.repoUrl(repoUrl)
        }
    }

    void bitbucketweb(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.BitbucketWeb') {
            delegate.url(url)
        }
    }

    void cgit(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.CGit') {
            delegate.url(url)
        }
    }

    void gitblit(String url, String projectName) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitBlitRepositoryBrowser') {
            delegate.url(url)
            delegate.projectName(projectName)
        }
    }

    void githubweb(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GithubWeb') {
            delegate.url(url)
        }
    }

    void gitiles(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Gitiles') {
            delegate.repoUrl(repoUrl)
        }
    }

    void gitlab(String url, String version) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitLab') {
            delegate.url(url)
            delegate.version(version)
        }
    }

    void gitlist(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitList') {
            delegate.repoUrl(repoUrl)
        }
    }

    void gitoriousweb(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitoriousWeb') {
            delegate.repoUrl(repoUrl)
        }
    }

    void gitweb(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GitWeb') {
            delegate.repoUrl(repoUrl)
        }
    }

    void phabricator(String repoUrl, String repo) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.Phabricator') {
            delegate.repoUrl(repoUrl)
            delegate.repo(repo)
        }
    }

    void redmineweb(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.RedmineWeb') {
            delegate.repoUrl(repoUrl)
        }
    }

    void rhodecode(String repoUrl) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.RhodeCode') {
            delegate.repoUrl(repoUrl)
        }
    }

    void viewgit(String repoUrl, String projectName) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.ViewGitWeb') {
            delegate.repoUrl(repoUrl)
            delegate.projectName(projectName)
        }
    }
}
