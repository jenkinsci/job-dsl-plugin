package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class GitPublisherContext extends AbstractContext {
    boolean pushOnlyIfSuccess
    boolean pushMerge
    boolean forcePush
    List<Node> tags = []
    List<Node> notes = []
    List<Node> branches = []

    GitPublisherContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Only pushes to remotes if the build succeeds. Defaults to {@code false}.
     */
    void pushOnlyIfSuccess(boolean pushOnlyIfSuccess = true) {
        this.pushOnlyIfSuccess = pushOnlyIfSuccess
    }

    /**
     * Pushes merges back to the origin specified in the pre-build merge options. Defaults to {@code false}.
     */
    void pushMerge(boolean pushMerge = true) {
        this.pushMerge = pushMerge
    }

    /**
     * Adds the force option to {@code git push}.
     *
     * @since 1.27
     */
    void forcePush(boolean forcePush = true) {
        this.forcePush = forcePush
    }

    /**
     * Adds a tag to push to a remote repository. Can be called multiple times to push more tags.
     */
    void tag(String targetRepo, String name, @DslContext(TagToPushContext) Closure closure = null) {
        checkNotNullOrEmpty(targetRepo, 'targetRepo must be specified')
        checkNotNullOrEmpty(name, 'name must be specified')

        TagToPushContext context = new TagToPushContext()
        ContextHelper.executeInContext(closure, context)

        tags << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher_-TagToPush' {
            targetRepoName(targetRepo)
            tagName(name)
            tagMessage(context.message ?: '')
            createTag(context.create)
            updateTag(context.update)
        }
    }

    /**
     * Adds a note to push to a remote repository. Can be called multiple times to push more notes.
     *
     * @since 1.66
     */
    void note(String targetRepo, String message, @DslContext(NoteToPushContext) Closure closure = null) {
        checkNotNullOrEmpty(targetRepo, 'targetRepo must be specified')
        checkNotNullOrEmpty(message, 'message must be specified')

        NoteToPushContext context = new NoteToPushContext()
        ContextHelper.executeInContext(closure, context)

        notes << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher_-NoteToPush' {
            targetRepoName(targetRepo)
            noteMsg(message)
            noteNamespace(context.namespace ?: 'master')
            noteReplace(context.replace)
        }
    }

    /**
     * Adds a branch to push to a remote repository. Can be called multiple times to push more branches.
     */
    void branch(String targetRepo, String name) {
        checkNotNullOrEmpty(targetRepo, 'targetRepo must be specified')
        checkNotNullOrEmpty(name, 'name must be specified')

        branches << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher_-BranchToPush' {
            targetRepoName(targetRepo)
            branchName(name)
        }
    }
}
