package javaposse.jobdsl.dsl.helpers.scm

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

class SvnContext implements Context {
    def static class Location {
        String url = null
        String local = '.'
    }

    def locations = []
    def checkoutstrategy = SvnCheckoutStrategy.Update
    def excludedregions = []
    def includedregions = []
    def excludedusers = []
    def excludedcommitmsgs = []
    def excludedrevprop = ''
    Closure browserXmlClosure = null
    Closure configureXmlClosure = null

    private validateBrowser() {
        Preconditions.checkState(browserXmlClosure == null, 'Can only specify one browser to be used with svn.')
    }

    /*
     * At least one location MUST be specified.
     * Additional locations can be specified by calling location() multiple times.
     *   svnUrl   - What to checkout from SVN.
     *   localDir - Destination directory relative to workspace.
     *              If not specified, defaults to '.'.
     */
    def location(String svnUrl, String localDir = '.') {
        locations << new Location(url:svnUrl, local:localDir)
    }

    /*
     * The checkout strategy that should be used.  This is a global setting for all
     * locations.
     *   strategy - Strategy to use. Possible values:
     *                SvnCheckoutStrategy.Update
     *                SvnCheckoutStrategy.Checkout
     *                SvnCheckoutStrategy.UpdateWithClean
     *                SvnCheckoutStrategy.UpdateWithRevert
     *
     * If no checkout strategy is configured, the default is SvnCheckoutStrategy.Update.
     */
    def checkoutStrategy(SvnCheckoutStrategy strategy) {
        checkoutstrategy = strategy
    }

    /*
     * Add an excluded region.  Each call to excludedRegion() adds to the list of
     * excluded regions.
     * If excluded regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that match the specified
     * patterns when determining if a build needs to be triggered.
     *   pattern - RegEx
     */
    def excludedRegion(String pattern) {
        excludedregions << pattern
    }

    /*
     * Add a list of excluded regions.  Each call to excludedRegions() adds to the
     * list of excluded regions.
     * If excluded regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that match the specified
     * patterns when determining if a build needs to be triggered.
     *   patterns - RegEx
     */
    def excludedRegions(Iterable<String> patterns) {
        patterns.each {
            excludedRegion(it)
        }
    }

    /*
     * Add an included region.  Each call to includedRegion() adds to the list of
     * included regions.
     * If included regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that do _not_ match the specified
     * patterns when determining if a build needs to be triggered.
     *   pattern - RegEx
     */
    def includedRegion(String pattern) {
        includedregions << pattern
    }

    /*
     * Add a list of included regions.  Each call to includedRegions() adds to the
     * list of included regions.
     * If included regions are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any files and/or folders that do _not_ match the specified
     * patterns when determining if a build needs to be triggered.
     *   patterns - RegEx
     */
    def includedRegions(Iterable<String> patterns) {
        patterns.each {
            includedRegion(it)
        }
    }

    /*
     * Add an excluded user.  Each call to excludedUser() adds to the list of
     * excluded users.
     * If excluded users are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions committed by the specified users when
     * determining if a build needs to be triggered.
     *   user - User to ignore when triggering builds
     */
    def excludedUser(String user) {
        excludedusers << user
    }

    /*
     * Add a list of excluded users.  Each call to excludedUsers() adds to the
     * list of excluded users.
     * If excluded users are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions committed by the specified users when
     * determining if a build needs to be triggered.
     *   users - Users to ignore when triggering builds
     */
    def excludedUsers(Iterable<String> users) {
        users.each {
            excludedUser(it)
        }
    }

    /*
     * Add an exluded commit message.  Each call to excludedCommitMsg() adds to the list of
     * excluded commit messages.
     * If excluded messages are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions with commit messages that match the specified
     * patterns when determining if a build needs to be triggered.
     *   pattern - RegEx
     */
    def excludedCommitMsg(String pattern) {
        excludedcommitmsgs << pattern
    }

    /*
     * Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the
     * list of excluded commit messages.
     * If excluded messages are configured, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions with commit messages that match the specified
     * patterns when determining if a build needs to be triggered.
     *   patterns - RegEx
     */
    def excludedCommitMsgs(Iterable<String> patterns) {
        patterns.each {
            excludedCommitMsg(it)
        }
    }

    /*
     * Set an excluded revision property.
     * If an excluded revision property is set, and Jenkins is set to poll for changes,
     * Jenkins will ignore any revisions that are marked with the specified
     * revision property when determining if a build needs to be triggered.
     * This only works in Subversion 1.5 servers or greater.
     *   pattern - RegEx
     */
    def excludedRevProp(String revisionProperty) {
        excludedrevprop = revisionProperty
    }

    /*
     * Create a closure for building a browser node with a single url node.
     */
    private Closure basicBrowserXml(String className, String url) {
        return { svnNode ->
            svnNode << browser('class':className) {
                delegate.url url
            }
        }
    }

    /*
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

    /*
     * CollabNet Browser
     * <browser class="hudson.scm.browsers.CollabNetSVN">
     *     <url>http://url/</url>
     * </browser>
     */
    def browserCollabnetSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.CollabNetSVN', url)
    }

    /*
     * FishEye Browser
     * <browser class="hudson.scm.browsers.FishEyeSVN">
     *     <url>http://url/browse/foobar/</url>
     *     <rootModule>rootModule</rootModule>
     * </browser>
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

    /*
     * SVN::Web Browser
     * <browser class="hudson.scm.browsers.SVNWeb">
     *     <url>http://url/</url>
     * </browser>
     */
    def browserSvnWeb(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.SVNWeb', url)
    }

    /*
     * Sventon 1.x Browser
     * <browser class="hudson.scm.browsers.Sventon">
     *     <url>http://url/</url>
     *     <repositoryInstance>repoInstance</repositoryInstance>
     * </browser>
     */
    def browserSventon(String url, String repoInstance) {
        validateBrowser()
        browserXmlClosure = sventonBrowserXml('hudson.scm.browsers.Sventon', url, repoInstance)
    }

    /*
     * Sventon 2.x Browser
     * <browser class="hudson.scm.browsers.Sventon2">
     *     <url>http://url/</url>
     *     <repositoryInstance>repoInstance</repositoryInstance>
     * </browser>
     */
    def browserSventon2(String url, String repoInstance) {
        validateBrowser()
        browserXmlClosure = sventonBrowserXml('hudson.scm.browsers.Sventon2', url, repoInstance)
    }

    /*
     * ViewSVN Browser
     * <browser class="hudson.scm.browsers.ViewSVN">
     *     <url>http://url/</url>
     * </browser>
     */
    def browserViewSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.ViewSVN', url)
    }

    /*
     * WebSVN Browser
     * <browser class="hudson.scm.browsers.WebSVN">
     *     <url>http://url/</url>
     * </browser>
     */
    def browserWebSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.WebSVN', url)
    }

    /*
     * Sets a closure to be called when the XML node structure is created.
     * The SVN node is passed to the closure as the first parameter.
     */
    void configure(Closure withXmlClosure) {
        this.configureXmlClosure = withXmlClosure
    }
}
