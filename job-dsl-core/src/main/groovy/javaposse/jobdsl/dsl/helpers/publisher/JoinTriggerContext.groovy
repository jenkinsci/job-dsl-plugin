package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class JoinTriggerContext extends AbstractContext {
    final List<String> projects = []
    final PublisherContext publisherContext
    boolean evenIfDownstreamUnstable

    protected JoinTriggerContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        publisherContext = new PublisherContext(jobManagement, item)
    }

    /**
     * Specifies the projects to run after all immediate downstream jobs have completed.
     */
    void projects(String... projects) {
        this.projects.addAll(projects)
    }

    /**
     * Adds publishers to run after all immediate downstream jobs have completed.
     *
     * Currently only the {@link PublisherContext#downstreamParameterized(groovy.lang.Closure) downstreamParameterized}
     * publisher is supported by the Join Plugin.
     */
    void publishers(@DslContext(PublisherContext) Closure publisherClosure) {
        ContextHelper.executeInContext(publisherClosure, publisherContext)
        Preconditions.checkArgument(
                publisherContext.publisherNodes.every {
                    it.name() == 'hudson.plugins.parameterizedtrigger.BuildTrigger'
                },
                'join plugin only supports downstreamParameterized publisher'
        )
    }

    /**
     * If set, runs the projects even if the downstream jobs are unstable. Defaults to {@code false}.
     */
    void evenIfDownstreamUnstable(boolean evenIfDownstreamUnstable = true) {
        this.evenIfDownstreamUnstable = evenIfDownstreamUnstable
    }
}
