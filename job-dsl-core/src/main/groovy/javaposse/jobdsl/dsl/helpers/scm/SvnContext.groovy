package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context

class SvnContext implements Context {
    List<Node> locations = []
    SvnCheckoutStrategy checkoutStrategy = SvnCheckoutStrategy.Update
    List<String> excludedRegions = []
    List<String> includedRegions = []
    List<String> excludedUsers = []
    List<String> excludedCommitMessages = []
    String excludedRevisionProperty
    Closure configureClosure

    /**
     * At least one location must be specified. Additional locations can be specified by calling {@link #location}
     * multiple times.
     *
     * @param svnUrl the URL of the repository to be checked out.
     * @param localDir destination directory of checkout, relative to workspace
     */
    void location(String svnUrl, String localDir = '.') {
        locations << new NodeBuilder().'hudson.scm.SubversionSCM_-ModuleLocation' {
            remote(svnUrl)
            local(localDir)
        }
    }

    /**
     * The checkout strategy that should be used.  This is a global setting for all locations. If no checkout strategy
     * is configured, the default is {@link SvnCheckoutStrategy#Update}.
     *
     * @param strategy the strategy to use, see {@link SvnCheckoutStrategy}
     */
    void checkoutStrategy(SvnCheckoutStrategy strategy) {
        checkoutStrategy = strategy
    }

    /**
     * Add a list of excluded regions.  Each call adds to the list of excluded regions. If excluded
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded regions.
     */
    void excludedRegions(String... patterns) {
        excludedRegions.addAll(patterns)
    }

    /**
     * Add a list of excluded regions.  Each call adds to the list of excluded regions. If excluded
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded regions.
     */
    void excludedRegions(Iterable<String> patterns) {
        excludedRegions += patterns
    }

    /**
     * Add a list of included regions.  Each call adds to the list of included regions. If included
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * do _not_ match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns a list of regular expressions that should be matched as part of the included regions.
     */
    void includedRegions(String... patterns) {
        includedRegions.addAll(patterns)
    }

    /**
     * Add a list of included regions.  Each call adds to the list of included regions. If included
     * regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that
     * do _not_ match the specified patterns when determining if a build needs to be triggered.
     *
     * @param patterns a list of regular expressions that should be matched as part of the included regions.
     */
    void includedRegions(Iterable<String> patterns) {
        includedRegions += patterns
    }

    /**
     * Add a list of excluded users.  Each call adds to the list of excluded users. If excluded users
     * are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the
     * specified users when determining if a build needs to be triggered.
     *
     * @param users a list of users to ignore when triggering builds.
     */
    void excludedUsers(String... users) {
        excludedUsers.addAll(users)
    }

    /**
     * Add a list of excluded users.  Each call adds to the list of excluded users. If excluded users
     * are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the
     * specified users when determining if a build needs to be triggered.
     *
     * @param users a list of users to ignore when triggering builds.
     */
    void excludedUsers(Iterable<String> users) {
        excludedUsers += users
    }

    /**
     * Add a list of excluded commit messages.  Each call adds to the list of excluded commit
     * messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any
     * revisions with commit messages that match the specified patterns when determining if a build needs to be
     * triggered.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMessages(String... patterns) {
        excludedCommitMessages.addAll(patterns)
    }

    /**
     * Add a list of excluded commit messages.  Each call adds to the list of excluded commit
     * messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any
     * revisions with commit messages that match the specified patterns when determining if a build needs to be
     * triggered.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMessages(Iterable<String> patterns) {
        excludedCommitMessages += patterns
    }

    /**
     * Set an excluded revision property.
     * If an excluded revision property is set, and Jenkins is set to poll for changes, Jenkins will ignore any
     * revisions that are marked with the specified revision property when determining if a build needs to be triggered.
     * This only works in Subversion 1.5 servers or greater.
     *
     * @param revisionProperty the revision property checked when triggering builds.
     */
    void excludedRevisionProperty(String revisionProperty) {
        excludedRevisionProperty = revisionProperty
    }

    /**
     * Sets a closure to be called when the XML node structure is created.
     * The SVN node is passed to the closure as the first parameter.
     *
     * @param closure closure used to perform additional configuration on the generated SVN node
     */
    void configure(Closure closure) {
        this.configureClosure = closure
    }
}
