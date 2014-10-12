package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.ContextHelper
import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class GitPublisherContext implements Context {
    boolean pushOnlyIfSuccess
    boolean pushMerge
    List<Node> tags = []
    List<Node> branches = []

    void pushOnlyIfSuccess(boolean pushOnlyIfSuccess = true) {
        this.pushOnlyIfSuccess = pushOnlyIfSuccess
    }

    void pushMerge(boolean pushMerge = true) {
        this.pushMerge = pushMerge
    }

    /**
     * <hudson.plugins.git.GitPublisher_-TagToPush>
     *     <targetRepoName>origin</targetRepoName>
     *     <tagName>foo-$PIPELINE_VERSION</tagName>
     *     <tagMessage>Release $PIPELINE_VERSION</tagMessage>
     *     <createTag>true</createTag>
     *     <updateTag>false</updateTag>
     * </hudson.plugins.git.GitPublisher_-TagToPush>
     */
    void tag(String targetRepo, String name, Closure closure = null) {
        checkArgument(!isNullOrEmpty(targetRepo), 'targetRepo must be specified')
        checkArgument(!isNullOrEmpty(name), 'name must be specified')

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
     * <hudson.plugins.git.GitPublisher_-BranchToPush>
     *     <targetRepoName>origin</targetRepoName>
     *     <branchName>master</branchName>
     * </hudson.plugins.git.GitPublisher_-BranchToPush>
     */
    void branch(String targetRepo, String name) {
        checkArgument(!isNullOrEmpty(targetRepo), 'targetRepo must be specified')
        checkArgument(!isNullOrEmpty(name), 'name must be specified')

        branches << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher_-BranchToPush' {
            targetRepoName(targetRepo)
            branchName(name)
        }
    }
}
