package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.views.gridbuilder.DownstreamProjectGridBuilderContext

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext
import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

class BuildPipelineView extends View {
    BuildPipelineView(JobManagement jobManagement) {
        super(jobManagement)
    }

    void displayedBuilds(int displayedBuilds) {
        checkArgument(displayedBuilds > 0, 'displayedBuilds must be greater than zero')

        execute {
            it / noOfDisplayedBuilds(displayedBuilds)
        }
    }

    /**
     * <filterExecutors>true</filterExecutors>
     */
    void filterExecutors(boolean filterExecutors = false) {
        execute {
            it / methodMissing('filterExecutors', filterExecutors)
        }
    }

    /**
     * <filterQueue>true</filterQueue>
     */
    void filterQueue(boolean filterQueue = false) {
        execute {
            it / methodMissing('filterQueue', filterQueue)
        }
    }

    void downstreamProject(@DslContext(DownstreamProjectGridBuilderContext) Closure gridBuilderClosure) {
        DownstreamProjectGridBuilderContext gridBuilderContext = new DownstreamProjectGridBuilderContext()
        executeInContext(gridBuilderClosure, gridBuilderContext)
        Node aGridBuilder = NodeBuilder.newInstance().
                gridBuilder(class: 'au.com.centrumsystems.hudson.plugin.buildpipeline.DownstreamProjectGridBuilder') {
            firstJob gridBuilderContext.firstJob
        }
        execute {
            it << aGridBuilder
        }
    }

    void title(String title) {
        execute {
            it / buildViewTitle(title ?: '')
        }
    }

    void selectedJob(String selectedJob) {
        checkNotNull(selectedJob, 'selectedJob must not be null')

        execute {
            it / methodMissing('selectedJob', selectedJob)
        }
    }

    void consoleOutputLinkStyle(OutputStyle outputStyle) {
        checkNotNull(outputStyle, 'consoleOutputLinkStyle must not be null')

        execute {
            it / methodMissing('consoleOutputLinkStyle', outputStyle.value)
        }
    }

    void customCssUrl(String customCssUrl) {
        execute {
            it / cssUrl(customCssUrl ?: '')
        }
    }

    void triggerOnlyLatestJob(boolean triggerOnlyLatestJob = true) {
        execute {
            it / methodMissing('triggerOnlyLatestJob', triggerOnlyLatestJob)
        }
    }

    void alwaysAllowManualTrigger(boolean alwaysAllowManualTrigger = true) {
        execute {
            it / methodMissing('alwaysAllowManualTrigger', alwaysAllowManualTrigger)
        }
    }

    void showPipelineParameters(boolean showPipelineParameters = true) {
        execute {
            it / methodMissing('showPipelineParameters', showPipelineParameters)
        }
    }

    void showPipelineParametersInHeaders(boolean showPipelineParametersInHeaders = true) {
        execute {
            it / methodMissing('showPipelineParametersInHeaders', showPipelineParametersInHeaders)
        }
    }

    void refreshFrequency(int refreshFrequency) {
        checkArgument(refreshFrequency > 0, 'refreshFrequency must be greater than zero')

        execute {
            it / methodMissing('refreshFrequency', refreshFrequency)
        }
    }

    void showPipelineDefinitionHeader(boolean showPipelineDefinitionHeader = true) {
        execute {
            it / methodMissing('showPipelineDefinitionHeader', showPipelineDefinitionHeader)
        }
    }

    /**
     * @since 1.26
     */
    void startsWithParameters(boolean startsWithParameters = true) {
        execute {
            it / methodMissing('startsWithParameters', startsWithParameters)
        }
    }

    static enum OutputStyle {
        Lightbox('Lightbox'),
        NewWindow('New Window'),
        ThisWindow('This Window')

        final String  value

        OutputStyle(String value) {
            this.value = value
        }
    }
}
