package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

@ContextType('hudson.scm.RepositoryBrowser')
class SvnBrowserContext extends AbstractExtensibleContext {
    Node browser

    protected SvnBrowserContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Use Assembla as repository browser.
     *
     * @since 1.78
     */
    void assembla(String spaceName) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.Assembla') {
            delegate.spaceName(spaceName)
        }
    }

    /**
     * Use CollabNet SVN as repository browser.
     *
     * @since 1.78
     */
    void collabNetSVN(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.CollabNetSVN') {
            delegate.url(url)
        }
    }

    /**
     * Use FishEye SVN as repository browser.
     *
     * @since 1.78
     */
    void fishEyeSVN(String url, String rootModule) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.FishEyeSVN') {
            delegate.url(url)
            delegate.rootModule(rootModule)
        }
    }

    /**
     * Use Phabricator as repository browser.
     *
     * @since 1.78
     */
    void phabricator(String url, String repo) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.Phabricator') {
            delegate.url(url)
            delegate.repo(repo)
        }
    }

    /**
     * Use Sventon as repository browser.
     *
     * @since 1.78
     */
    void sventon(String url, String repositoryInstance) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.Sventon') {
            delegate.url(url)
            delegate.repositoryInstance(repositoryInstance)
        }
    }

    /**
     * Use Sventon2 as repository browser.
     *
     * @since 1.78
     */
    void sventon2(String url, String repositoryInstance) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.Sventon2') {
            delegate.url(url)
            delegate.repositoryInstance(repositoryInstance)
        }
    }

    /**
     * Use SVN::Web as repository browser.
     *
     * @since 1.78
     */
    void svnWeb(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.SVNWeb') {
            delegate.url(url)
        }
    }

    /**
     * Use ViewSVN as repository browser.
     *
     * @since 1.78
     */
    void viewSVN(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.ViewSVN') {
            delegate.url(url)
        }
    }

    /**
     * Use VisualSVN as repository browser.
     *
     * @since 1.78
     */
    void visualSVN(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.VisualSVN') {
            delegate.url(url)
        }
    }

    /**
     * Use WebSVN as repository browser.
     *
     * @since 1.78
     */
    void webSVN(String url) {
        browser = NodeBuilder.newInstance().browser(class: 'hudson.scm.browsers.WebSVN') {
            delegate.url(url)
        }
    }

    @Override
    protected void addExtensionNode(Node node) {
        browser = ContextHelper.toNamedNode('browser', node)
    }
}
