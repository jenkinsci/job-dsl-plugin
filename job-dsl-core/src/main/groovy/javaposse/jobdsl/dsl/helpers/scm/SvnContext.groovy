package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

import static com.google.common.base.Preconditions.checkState

class SvnContext implements Context {

    static class Location {
        String url = null
        String local = '.'
    }

    List<Location> locations = []
    SvnCheckoutStrategy checkoutStrategy = SvnCheckoutStrategy.Update
    List<String> excludedRegions = []
    List<String> includedRegions = []
    List<String> excludedUsers = []
    List<String> excludedCommitMsgs = []
    String excludedRevProp = ''
    Closure browserXmlClosure = null
    Closure configureXmlClosure = null

    /**
     * Ensures that only one browser is specified per job.
     */
    private void validateBrowser() {
        checkState(browserXmlClosure == null, 'Can only specify one browser to be used with svn.')
    }

    /**
     * At least one location MUST be specified. Additional locations can be specified by calling location() multiple
     * times.
     *
     * @param svnUrl The URL of the repositoy to be checked out.
     * @param localDir Destination directory of checkout, relative to workspace. If not specified, defaults to '.'.
     */
    void location(String svnUrl, String localDir = '.') {
        locations << new Location(url: svnUrl, local: localDir)
    }

    /**
     * The checkout strategy that should be used.  This is a global setting for all locations. If no checkout strategy
     * is configured, the default is SvnCheckoutStrategy.Update.
     *
     * @param strategy Strategy to use. @see {@link SvnCheckoutStrategy}
     */
    void checkoutStrategy(SvnCheckoutStrategy strategy) {
        checkoutStrategy = strategy
    }

    /**
     * Add an excluded region.  Each call to excludedRegion() adds to the list of excluded regions. If excluded regions
     * are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that match
     * the specified patterns when determining if a build needs to be triggered.
     *
     * @param pattern A regular expression that should be matched as part of the excluded regions.
     */
    void excludedRegion(String pattern) {
        excludedRegions << pattern
    }

    /**
     * Add a list of excluded regions.  Each call to excludedRegions() adds to the list of excluded regions. If excluded
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns A list of regular expressions that should be matched as part of the excluded regions.
     */
    void excludedRegions(String... patterns) {
        patterns.each {
            excludedRegion(it)
        }
    }

    /**
     * Add a list of excluded regions.  Each call to excludedRegions() adds to the list of excluded regions. If excluded
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns A list of regular expressions that should be matched as part of the excluded regions.
     */
    void excludedRegions(Iterable<String> patterns) {
        excludedRegions(*patterns)
    }

    /**
     * Add an included region.  Each call to includedRegion() adds to the list of included regions. If included regions
     * are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that do
     * _not_ match the specified patterns when determining if a build needs to be triggered.
     *
     * @param pattern A regular expression that should be matched as part of the included regions.
     */
    void includedRegion(String pattern) {
        includedRegions << pattern
    }

    /**
     * Add a list of included regions.  Each call to includedRegions() adds to the list of included regions. If included
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * do _not_ match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns A list of regular expressions that should be matched as part of the included regions.
     */
    void includedRegions(String... patterns) {
        patterns.each {
            includedRegion(it)
        }
    }

    /**
     * Add a list of included regions.  Each call to includedRegions() adds to the list of included regions. If included
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * do _not_ match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns A list of regular expressions that should be matched as part of the included regions.
     */
    void includedRegions(Iterable<String> patterns) {
        includedRegions(*patterns)
    }

    /**
     * Add an excluded user.  Each call to excludedUser() adds to the list of excluded users. If excluded users are
     * configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the specified
     * users when determining if a build needs to be triggered.
     *
     * @param user User to ignore when triggering builds.
     */
    void excludedUser(String user) {
        excludedUsers << user
    }

    /**
     * Add a list of excluded users.  Each call to excludedUsers() adds to the list of excluded users. If excluded users
     * are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the
     * specified users when determining if a build needs to be triggered.
     *
     * @param users A list of users to ignore when triggering builds.
     */
    void excludedUsers(String... users) {
        users.each {
            excludedUser(it)
        }
    }

    /**
     * Add a list of excluded users.  Each call to excludedUsers() adds to the list of excluded users. If excluded users
     * are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the
     * specified users when determining if a build needs to be triggered.
     *
     * @param users A list of users to ignore when triggering builds.
     */
    void excludedUsers(Iterable<String> users) {
        excludedUsers(*users)
    }

    /**
     * Add an exluded commit message.  Each call to excludedCommitMsg() adds to the list of excluded commit messages. If
     * excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions with
     * commit messages that match the specified patterns when determining if a build needs to be triggered.
     *
     * @param pattern A regular expression that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMsg(String pattern) {
        excludedCommitMsgs << pattern
    }

    /**
     * Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the list of excluded commit
     * messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any
     * revisions with commit messages that match the specified patterns when determining if a build needs to be
     * triggered.
     *
     * @param patterns A list of regular expressions that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMsgs(String... patterns) {
        patterns.each {
            excludedCommitMsg(it)
        }
    }

    /**
     * Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the list of excluded commit
     * messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any
     * revisions with commit messages that match the specified patterns when determining if a build needs to be
     * triggered.
     *
     * @param patterns A list of regular expressions that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMsgs(Iterable<String> patterns) {
        excludedCommitMsgs(*patterns)
    }

    /**
     * Set an excluded revision property.
     * If an excluded revision property is set, and Jenkins is set to poll for changes, Jenkins will ignore any
     * revisions that are marked with the specified revision property when determining if a build needs to be triggered.
     * This only works in Subversion 1.5 servers or greater.
     *
     * @param revisionProperty The revision property checked when triggering builds.
     */
    void excludedRevProp(String revisionProperty) {
        excludedRevProp = revisionProperty
    }

    /**
     * Create a closure for building a browser node with a single url node.
     *
     * @param className Class name of browser node in XML.
     * @param url URL of browser.
     *
     * @return Closure for building browser node.
     */
    private Closure basicBrowserXml(String className, String url) {
        return { svnNode ->
            svnNode << browser(class: className) {
                delegate.url url
            }
        }
    }

    /**
     * Create a closure for building a browser node suitable for Sventon.
     *
     * @param className Class name of Sventon browser in XML.
     * @param url URL of Sventon repository browser.
     * @param repoInstance Sventon repository instance name.
     *
     * @return Closure for building Sventon browser node.
     */
    private Closure sventonBrowserXml(String className, String url, String repoInstance) {
        return { svnNode ->
            svnNode << browser(class: className) {
                delegate.url url
                delegate.repositoryInstance repoInstance
            }
        }
    }

    /**
     * Configure the job to use the CollabNet browser.
     *
     * @param url The repository browser URL for the root of the project. For example, a Java.net project called
     *            myproject would use https://myproject.dev.java.net/source/browse/myproject.
     */
    void browserCollabNet(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.CollabNetSVN', url)
    }

    /**
     * Configure the job to use the FishEye browser.
     *
     * @param url Specify the URL of this module in FishEye. (such as http://fisheye6.cenqua.com/browse/ant/).
     * @param rootModule Specify the root Subversion module that this FishEye monitors. For example, for
     *                   http://fisheye6.cenqua.com/browse/ant/, this field would be ant because it displays the
     *                   directory "/ant" of the ASF repo. If FishEye is configured to display the whole SVN repository,
     *                   leave this field empty.
     */
    void browserFishEye(String url, String rootModule = '') {
        validateBrowser()
        browserXmlClosure = { svnNode ->
            svnNode << browser(class: 'hudson.scm.browsers.FishEyeSVN') {
                delegate.url url
                delegate.rootModule rootModule
            }
        }
    }

    /**
     * Configure the job to use the SVN::Web browser.
     *
     * @param url Specify the URL of this module in SVN::Web.
     */
    void browserSvnWeb(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.SVNWeb', url)
    }

    /**
     * Configure the job to use the Sventon 1.x browser.
     *
     * @param url Specify the URL of the Sventon repository browser. For example, if you normally browse from
     *            http://somehost.com/svn/repobrowser.svn?name=local, this field would be http://somehost.com/svn/.
     * @param repoInstance Specify the Sventon repository instance name that references this subversion repository. For
     *                     example, if you normally browse from http://somehost.com/svn/repobrowser.svn?name=local, this
     *                     field would be local.
     */
    void browserSventon(String url, String repoInstance) {
        validateBrowser()
        browserXmlClosure = sventonBrowserXml('hudson.scm.browsers.Sventon', url, repoInstance)
    }

    /**
     * Configure the job to use the Sventon 2.x browser.
     *
     * @param url Specify the URL of the Sventon repository browser. For example, if you normally browse from
     *            http://somehost.com/svn/repobrowser.svn?name=local, this field would be http://somehost.com/svn/.
     * @param repoInstance Specify the Sventon repository instance name that references this subversion repository. For
     *                     example, if you normally browse from http://somehost.com/svn/repobrowser.svn?name=local, this
     *                     field would be local.
     */
    void browserSventon2(String url, String repoInstance) {
        validateBrowser()
        browserXmlClosure = sventonBrowserXml('hudson.scm.browsers.Sventon2', url, repoInstance)
    }

    /**
     * Configure the job to use the ViewSVN browser.
     *
     * @param url Specify the root URL of ViewSVN for this repository (such as http://svn.apache.org/viewvc).
     */
    void browserViewSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.ViewSVN', url)
    }

    /**
     * Configure the job to use the WebSVN browser.
     *
     * @param url Specify the URL of this module in WebSVN.
     */
    void browserWebSvn(String url) {
        validateBrowser()
        browserXmlClosure = basicBrowserXml('hudson.scm.browsers.WebSVN', url)
    }

    /**
     * Sets a closure to be called when the XML node structure is created.
     * The SVN node is passed to the closure as the first parameter.
     *
     * @param withXmlClosure Closure used to perform additional configuration on the generated SVN node. The closure is
     *                       passed the SVN SCM.
     */
    void configure(Closure withXmlClosure) {
        this.configureXmlClosure = withXmlClosure
    }
}
