package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
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
    void tag(Closure closure) {
        TagToPushContext context = new TagToPushContext()
        AbstractContextHelper.executeInContext(closure, context)
        
        checkArgument(!isNullOrEmpty(context.targetRepo), "targetRepo must be specified")
        checkArgument(!isNullOrEmpty(context.name), "name must be specified")

        tags << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher_-TagToPush' {
            targetRepoName(context.targetRepo)
            tagName(context.name)
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
    void branch(Closure closure) {
        RefToPushContext context = new RefToPushContext()
        AbstractContextHelper.executeInContext(closure, context)
        
        checkArgument(!isNullOrEmpty(context.targetRepo), "targetRepo must be specified")
        checkArgument(!isNullOrEmpty(context.name), "name must be specified")

        branches << NodeBuilder.newInstance().'hudson.plugins.git.GitPublisher_-BranchToPush' {
            targetRepoName(context.targetRepo)
            branchName(context.name)
        }
    }
}
