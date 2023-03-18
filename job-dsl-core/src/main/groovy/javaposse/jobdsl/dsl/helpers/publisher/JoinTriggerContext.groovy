package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.helpers.common.Threshold

class JoinTriggerContext extends AbstractContext {
    final List<String> projects = []
    final PublisherContext publisherContext
    String resultThreshold = 'SUCCESS'

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
     * The result threshold of the downstream projects to be checked. The join projects will be started only when all
     * downstream projects have results that are better then selected. Defaults to {@code 'SUCCESS'}.
     *
     * Possible thresholds are {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'} or {@code 'ABORTED'}.
     *
     * @since 1.61
     */
    void resultThreshold(String threshold) {
        Preconditions.checkArgument(
                Threshold.THRESHOLD_COLOR_MAP.containsKey(threshold),
                "threshold must be one of these values ${Threshold.THRESHOLD_COLOR_MAP.keySet().join(',')}"
        )

        this.resultThreshold = threshold
    }
}
