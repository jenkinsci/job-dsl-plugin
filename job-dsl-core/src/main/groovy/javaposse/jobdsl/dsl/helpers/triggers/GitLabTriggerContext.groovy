package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class GitLabTriggerContext extends AbstractContext {
    private static final Set<String> VALID_EXECUTION_STATUSES = ['never', 'source', 'both']

    String includeBranches
    String excludeBranches
    String targetBranchRegex
    String rebuildOpenMergeRequest = 'never'
    String branchFilterType = 'All'
    boolean buildOnMergeRequestEvents = true
    boolean buildOnPushEvents = true
    boolean enableCiSkip = true
    boolean setBuildDescription = true
    boolean addNoteOnMergeRequest = true
    boolean addVoteOnMergeRequest = true
    boolean useCiFeatures = false
    boolean acceptMergeRequestOnSuccess = false
    String noteRegex = 'Jenkins please retry a build'
    boolean skipWorkInProgressMergeRequest = true

    GitLabTriggerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Comma-separated list of source branches allowed to trigger a build from a push event.
     */
    void includeBranches(String includeBranches) {
        this.includeBranches = includeBranches
        this.branchFilterType = 'NameBasedFilter'
    }

    /**
     * Comma-separated list of source branches disabled to trigger a build from a push event.
     */
    void excludeBranches(String excludeBranches) {
        this.excludeBranches = excludeBranches
        this.branchFilterType = 'NameBasedFilter'
    }

    /**
     * The target branch regex allows to limit the execution of this job to
     * certain branches. Any branch matching the specified pattern triggers the
     * job.
     *
     * @since 1.47
     */
    void targetBranchRegex(String targetBranchRegex) {
        this.targetBranchRegex = targetBranchRegex
        this.branchFilterType = 'RegexBasedFilter'
    }

    /**
     * If set, builds on merge request events. Defaults to {@code true}.
     */
    void buildOnMergeRequestEvents(boolean buildOnMergeRequestEvents = true) {
        this.buildOnMergeRequestEvents = buildOnMergeRequestEvents
    }

    /**
     * If set, builds on push events request events. Defaults to {@code true}.
     */
    void buildOnPushEvents(boolean buildOnPushEvents = true) {
        this.buildOnPushEvents = buildOnPushEvents
    }

    /**
     * If set, enables {@code [ci-skip]}. Defaults to {@code true}.
     */
    void enableCiSkip(boolean enableCiSkip = true) {
        this.enableCiSkip = enableCiSkip
    }

    /**
     * If set, sets build description to build cause (eg. Merge request or Git Push). Defaults to {@code true}.
     */
    void setBuildDescription(boolean setBuildDescription = true) {
        this.setBuildDescription = setBuildDescription
    }

    /**
     * If set, adds a note with build status on merge requests. Defaults to {@code true}.
     */
    @Deprecated
    void addNoteOnMergeRequest(boolean addNoteOnMergeRequest = true) {
        this.addNoteOnMergeRequest = addNoteOnMergeRequest
    }

    /**
     * If set, adds a vote to note with build status on merge requests. Defaults to {@code true}.
     */
    @Deprecated
    void addVoteOnMergeRequest(boolean addVoteOnMergeRequest = true) {
        this.addVoteOnMergeRequest = addVoteOnMergeRequest
    }

    /**
     * If set, enables GitLab 8.1 CI features. Defaults to {@code false}.
     */
    @Deprecated
    void useCiFeatures(boolean useCiFeatures = true) {
        this.useCiFeatures = useCiFeatures
    }

    /**
     * If set, accepts merge request on success. Defaults to {@code false}.
     */
    @Deprecated
    void acceptMergeRequestOnSuccess(boolean acceptMergeRequestOnSuccess = true) {
        this.acceptMergeRequestOnSuccess = acceptMergeRequestOnSuccess
    }

    /**
     * Only rebuild open Merge Requests. Defaults to {@code 'never'}.
     *
     * Possible values are {@code 'never'}, {@code 'source'} and {@code 'both'}.
     */
    void rebuildOpenMergeRequest(String rebuildOpenMergeRequest) {
        checkArgument(
                VALID_EXECUTION_STATUSES.contains(rebuildOpenMergeRequest),
                "rebuildOpenMergeRequest must be one of ${VALID_EXECUTION_STATUSES.join(', ')}"
        )
        this.rebuildOpenMergeRequest = rebuildOpenMergeRequest
    }

    /**
     * When filled, commenting this phrase in the merge request will trigger a build.
     *
     * @since 1.58
     */
    @RequiresPlugin(id = 'gitlab-plugin', minimumVersion = '1.2.4')
    void commentTrigger(String commentTrigger) {
        this.noteRegex = commentTrigger
    }

    /**
     * If set, ignores work in progress pull requests.
     *
     * @since 1.58
     */
    @RequiresPlugin(id = 'gitlab-plugin', minimumVersion = '1.2.4')
    void skipWorkInProgressMergeRequest(boolean skipWorkInProgressMergeRequest = true) {
        this.skipWorkInProgressMergeRequest = skipWorkInProgressMergeRequest
    }
}
