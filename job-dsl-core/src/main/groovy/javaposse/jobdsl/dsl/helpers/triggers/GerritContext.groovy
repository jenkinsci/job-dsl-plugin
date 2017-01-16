package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

class GerritContext implements Context {
    GerritEventContext eventContext = new GerritEventContext()
    Closure configureBlock
    List projects = []

    Integer startedCodeReview = null
    Integer startedVerified = null

    Integer successfulCodeReview = null
    Integer successfulVerified = null

    Integer failedCodeReview = null
    Integer failedVerified = null

    Integer unstableCodeReview = null
    Integer unstableVerified = null

    Integer notBuiltCodeReview = null
    Integer notBuiltVerified = null

    /**
     * The Verified and Code Review vote to set in Gerrit when the build starts.
     *
     * Set an argument to {@code null} to use the default value.
     */
    void buildStarted(Integer verified, Integer codeReview) {
        startedVerified = verified
        startedCodeReview = codeReview
    }

    /**
     * The Verified and Code Review vote to set in Gerrit when the build is successful.
     *
     * Set an argument to {@code null} to use the default value.
     */
    void buildSuccessful(Integer verified, Integer codeReview) {
        successfulVerified = verified
        successfulCodeReview = codeReview
    }

    /**
     * The Verified and Code Review vote to set in Gerrit when the build fails.
     *
     * Set an argument to {@code null} to use the default value.
     */
    void buildFailed(Integer verified, Integer codeReview) {
        failedVerified = verified
        failedCodeReview = codeReview
    }

    /**
     * The Verified and Code Review vote to set in Gerrit when the build is unstable.
     *
     * Set an argument to {@code null} to use the default value.
     */
    void buildUnstable(Integer verified, Integer codeReview) {
        unstableVerified = verified
        unstableCodeReview = codeReview
    }

    /**
     * The Verified and Code Review vote to set in Gerrit when no build was built.
     *
     * Set an argument to {@code null} to use the default value.
     */
    void buildNotBuilt(Integer verified, Integer codeReview) {
        notBuiltVerified = verified
        notBuiltCodeReview = codeReview
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code GerritTrigger} node is passed into the configure
     * block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }

    /**
     * Specifies which type of Gerrit events should trigger the build.
     */
    void events(@DslContext(GerritEventContext) Closure eventClosure) {
        ContextHelper.executeInContext(eventClosure, eventContext)
    }

    /**
     * Specifies on which Gerrit projects to trigger a build on. Use a {@code '&lt;type&gt;:&lt;pattern&gt;'} notation
     * to specify a project or branch name pattern. Supported types are {@code plain} (default), {@code ant} (called
     * "Path" in the UI) and {@code reg_exp}.
     */
    void project(String projectName, List<String> branches) {
        projects << [
                new GerritSpec(projectName),
                branches.collect { new GerritSpec(it) }
        ]
    }

    /**
     * Specifies on which Gerrit projects to trigger a build on. Use a {@code '&lt;type&gt;:&lt;pattern&gt;'} notation
     * to specify a project or branch name pattern. Supported types are {@code plain} (default), {@code ant} (called
     * "Path" in the UI) and {@code reg_exp}.
     */
    void project(String projectName, String branch) {
        project(projectName, [branch])
    }

    static class GerritSpec {
        GerritSpec(String raw) {
            int idx = raw.indexOf(':')
            String prefix = (idx == -1) ? '' : raw[0..(idx - 1)].toUpperCase()
            if (availableTypes.contains(prefix)) {
                type = prefix
                pattern = raw[(idx + 1)..-1]
            } else {
                type = 'PLAIN'
                pattern = raw
            }
        }

        Set<String> availableTypes = ['ANT', 'PLAIN', 'REG_EXP']
        String type
        String pattern
    }
}
