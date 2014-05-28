package javaposse.jobdsl.dsl.helpers.scm

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

class SvnContext implements Context {
    def static class Location {
        String url = null
        String local = '.'
    }

    def locations = []
    def checkoutStrategy = SvnCheckoutStrategy.Update
    def excludedRegions = []
    def includedRegions = []
    def excludedUsers = []
    def excludedCommitMsgs = []
    def excludedRevProp = ''
    Closure browserXmlClosure = null
    Closure configureXmlClosure = null

    private validateBrowser() {
        Preconditions.checkState(browserXmlClosure == null, 'Can only specify one browser to be used with svn.')
    }

    /**
     * At least one location MUST be specified.
     * Additional locations can be specified by calling location() multiple times.
     * @param svnUrl What to checkout from SVN.
     * @param localDir Destination directory relative to workspace. If not specified, defaults to '.'.
     */
    def location(String svnUrl, String localDir = '.') {
        locations << new Location(url:svnUrl, local:localDir)
    }

    /**
     * The checkout strategy that should be used.  This is a global setting for all
     * locations.
     * If no checkout strategy is configured, the default is SvnCheckoutStrategy.Update.
     * @param strategy Strategy to use. 
     * @see {@link SvnCheckoutStrategy}
     */
    def checkoutStrategy(SvnCheckoutStrategy strategy) {
        checkoutStrategy = strategy
    }

    /**
     * Add an excluded region.  Each call to excludedRegion() adds to the list of
     * excluded regions.
     * If excluded regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that match the specified
     * patterns when determining if a build needs to be triggered.
     * @param pattern RegEx
     */
    def excludedRegion(String pattern) {
        excludedRegions << pattern
    }

    /**
     * Add a list of excluded regions.  Each call to excludedRegions() adds to the
     * list of excluded regions.
     * If excluded regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that match the specified
     * patterns when determining if a build needs to be triggered.
     * @param patterns RegEx
     */
    def excludedRegions(Iterable<String> patterns) {
        patterns.each {
            excludedRegion(it)
        }
    }

    /**
     * Add an included region.  Each call to includedRegion() adds to the list of
     * included regions.
     * If included regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that do _not_ match the specified
     * patterns when determining if a build needs to be triggered.
     * @param pattern RegEx
     */
    def includedRegion(String pattern) {
        includedRegions << pattern
    }

    /**
     * Add a list of included regions.  Each call to includedRegions() adds to the
     * list of included regions.
     * If included regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that do _not_ match the specified
     * patterns when determining if a build needs to be triggered.
     * @param patterns RegEx
     */
    def includedRegions(Iterable<String> patterns) {
        patterns.each {
            includedRegion(it)
        }
    }

    /**
     * Add an excluded user.  Each call to excludedUser() adds to the list of
     * excluded users.
     * If excluded users are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions committed by the specified users when
     * determining if a build needs to be triggered.
     * @param user User to ignore when triggering builds
     */
    def excludedUser(String user) {
        excludedUsers << user
    }

    /**
     * Add a list of excluded users.  Each call to excludedUsers() adds to the
     * list of excluded users.
     * If excluded users are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions committed by the specified users when
     * determining if a build needs to be triggered.
     * @param users Users to ignore when triggering builds
     */
    def excludedUsers(Iterable<String> users) {
        users.each {
            excludedUser(it)
        }
    }

    /**
     * Add an exluded commit message.  Each call to excludedCommitMsg() adds to the list of
     * excluded commit messages.
     * If excluded messages are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions with commit messages that match the specified
     * patterns when determining if a build needs to be triggered.
     * @param pattern RegEx
     */
    def excludedCommitMsg(String pattern) {
        excludedCommitMsgs << pattern
    }

    /**
     * Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the
     * list of excluded commit messages.
     * If excluded messages are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions with commit messages that match the specified
     * patterns when determining if a build needs to be triggered.
     * @param patterns RegEx
     */
    def excludedCommitMsgs(Iterable<String> patterns) {
        patterns.each {
            excludedCommitMsg(it)
        }
    }

    /**
     * Set an excluded revision property.
     * If an excluded revision property is set, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions that are marked with the specified
     * revision property when determining if a build needs to be triggered.
     * This only works in Subversion 1.5 servers or greater.
     * @param pattern RegEx
     */
    def excludedRevProp(String revisionProperty) {
        excludedRevProp = revisionProperty
    }

    /**
     * Create a closure for building a browser node with a single url node.
     */
    private Closure basicBrowserXml(String className, String url) {
        return { svnNode ->
            svnNode << browser('class':className) {
                delegate.url url
            }
        }
    }

    /**
     * Create a closure for building a browser node suitable for Sventon.
     */
    private Closure sventonBrowserXml(String className, String url, String repoInstance) {
        return { svnNode ->
            svnNode << browser('class':className) {
                delegate.url url
                delegate.repositoryInstance repoInstance
            }
        }
    }

    /**
     * CollabNet Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.CollabNetSVN">
     *     <url>http://url/</url>
     * </browser>
     * }
     * </pre>
     */
    def browserCollabnetSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.CollabNetSVN', url)
    }

    /**
     * FishEye Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.FishEyeSVN">
     *     <url>http://url/browse/foobar/</url>
     *     <rootModule>rootModule</rootModule>
     * </browser>
     * }
     * </pre>
     */
    def browserFishEye(String url, String rootModule) {
        validateBrowser()
        browserXmlClosure = { svnNode ->
            svnNode << browser('class':'hudson.scm.browsers.FishEyeSVN') {
                delegate.url url
                delegate.rootModule rootModule
            }
        }
    }

    /**
     * SVN::Web Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.SVNWeb">
     *     <url>http://url/</url>
     * </browser>
     * }
     * </pre>
     */
    def browserSvnWeb(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.SVNWeb', url)
    }

    /**
     * Sventon 1.x Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.Sventon">
     *     <url>http://url/</url>
     *     <repositoryInstance>repoInstance</repositoryInstance>
     * </browser>
     * }
     * </pre>
     */
    def browserSventon(String url, String repoInstance) {
        validateBrowser()
        browserXmlClosure = sventonBrowserXml('hudson.scm.browsers.Sventon', url, repoInstance)
    }

    /**
     * Sventon 2.x Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.Sventon2">
     *     <url>http://url/</url>
     *     <repositoryInstance>repoInstance</repositoryInstance>
     * </browser>
     * }
     * </pre>
     */
    def browserSventon2(String url, String repoInstance) {
        validateBrowser()
        browserXmlClosure = sventonBrowserXml('hudson.scm.browsers.Sventon2', url, repoInstance)
    }

    /**
     * ViewSVN Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.ViewSVN">
     *     <url>http://url/</url>
     * </browser>
     * }
     * </pre>
     */
    def browserViewSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.ViewSVN', url)
    }

    /**
     * WebSVN Browser
     * <pre>
     * {@code
     * <browser class="hudson.scm.browsers.WebSVN">
     *     <url>http://url/</url>
     * </browser>
     * }
     * </pre>
     */
    def browserWebSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.WebSVN', url)
    }

    /**
     * Sets a closure to be called when the XML node structure is created.
     * The SVN node is passed to the closure as the first parameter.
     */
    void configure(Closure withXmlClosure) {
        this.configureXmlClosure = withXmlClosure
    }
}
