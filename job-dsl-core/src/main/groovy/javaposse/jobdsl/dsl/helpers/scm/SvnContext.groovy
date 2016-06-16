package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class SvnContext extends AbstractContext {
    List<Node> locations = []
    SvnCheckoutStrategy checkoutStrategy = SvnCheckoutStrategy.UPDATE
    List<String> excludedRegions = []
    List<String> includedRegions = []
    List<String> excludedUsers = []
    List<String> excludedCommitMessages = []
    String excludedRevisionProperty
    Closure configureBlock

    SvnContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * At least one location must be specified. Additional locations can be specified by calling {@link #location}
     * multiple times.
     *
     * @param svnUrl the URL of the repository to be checked out.
     * @param svnLocationClosure closure to specify option settings for the location
     */
    void location(String svnUrl, @DslContext(SvnLocationContext) Closure svnLocationClosure = null) {
        SvnLocationContext svnLocationContext = new SvnLocationContext(jobManagement)
        ContextHelper.executeInContext(svnLocationClosure, svnLocationContext)

        locations << new NodeBuilder().'hudson.scm.SubversionSCM_-ModuleLocation' {
            remote(svnUrl)
            if (svnLocationContext.credentials) {
                credentialsId(svnLocationContext.credentials)
            }
            local(svnLocationContext.directory)
            depthOption(svnLocationContext.depth.xmlValue)
            ignoreExternalsOption(svnLocationContext.ignoreExternals)
        }
    }

    /**
     * The checkout strategy that should be used.  This is a global setting for all locations. Defaults to
     * {@code SvnCheckoutStrategy.UPDATE}.
     *
     * @param strategy the strategy to use, see {@link SvnCheckoutStrategy}
     */
    void checkoutStrategy(SvnCheckoutStrategy strategy) {
        checkoutStrategy = strategy
    }

    /**
     * Add a list of excluded regions.  Each call adds to the list of excluded regions.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded regions.
     */
    void excludedRegions(String... patterns) {
        excludedRegions.addAll(patterns)
    }

    /**
     * Add a list of excluded regions.  Each call adds to the list of excluded regions.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded regions.
     */
    void excludedRegions(Iterable<String> patterns) {
        excludedRegions += patterns
    }

    /**
     * Add a list of included regions.  Each call adds to the list of included regions.
     *
     * @param patterns a list of regular expressions that should be matched as part of the included regions.
     */
    void includedRegions(String... patterns) {
        includedRegions.addAll(patterns)
    }

    /**
     * Add a list of included regions.  Each call adds to the list of included regions.
     *
     * @param patterns a list of regular expressions that should be matched as part of the included regions.
     */
    void includedRegions(Iterable<String> patterns) {
        includedRegions += patterns
    }

    /**
     * Add a list of excluded users.  Each call adds to the list of excluded users.
     *
     * @param users a list of users to ignore when triggering builds.
     */
    void excludedUsers(String... users) {
        excludedUsers.addAll(users)
    }

    /**
     * Add a list of excluded users.  Each call adds to the list of excluded users.
     *
     * @param users a list of users to ignore when triggering builds.
     */
    void excludedUsers(Iterable<String> users) {
        excludedUsers += users
    }

    /**
     * Add a list of excluded commit messages.  Each call adds to the list of excluded commit
     * messages.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMessages(String... patterns) {
        excludedCommitMessages.addAll(patterns)
    }

    /**
     * Add a list of excluded commit messages.  Each call adds to the list of excluded commit
     * messages.
     *
     * @param patterns a list of regular expressions that should be matched as part of the excluded commit messages.
     */
    void excludedCommitMessages(Iterable<String> patterns) {
        excludedCommitMessages += patterns
    }

    /**
     * Set an excluded revision property.
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
     * @param configureBlock closure used to perform additional configuration on the generated SVN node
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
}
