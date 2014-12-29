package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class GitPublisherContext implements Context {
    private final JobManagement jobManagement
    boolean pushOnlyIfSuccess
    boolean pushMerge
    boolean forcePush
    List<Node> tags = []
    List<Node> branches = []

    GitPublisherContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void pushOnlyIfSuccess(boolean pushOnlyIfSuccess = true) {
        this.pushOnlyIfSuccess = pushOnlyIfSuccess
    }

    void pushMerge(boolean pushMerge = true) {
        this.pushMerge = pushMerge
    }

    void forcePush(boolean forcePush = true) {
        jobManagement.requireMinimumPluginVersion('git', '2.2.6')
        this.forcePush = forcePush
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
    void tag(String targetRepo, String name, @DslContext(TagToPushContext) Closure closure = null) {
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
