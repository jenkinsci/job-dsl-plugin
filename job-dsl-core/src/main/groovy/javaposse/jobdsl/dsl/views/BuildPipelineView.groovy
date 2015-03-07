package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

class BuildPipelineView extends View {
    BuildPipelineView(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * <noOfDisplayedBuilds>5</noOfDisplayedBuilds>
     */
    void displayedBuilds(int displayedBuilds) {
        checkArgument(displayedBuilds > 0, 'displayedBuilds must be greater than zero')

        execute {
            it / noOfDisplayedBuilds(displayedBuilds)
        }
    }

    /**
     * <buildViewTitle>Project A</buildViewTitle>
     */
    void title(String title) {
        execute {
            it / buildViewTitle(title ?: '')
        }
    }

    /**
     * <selectedJob>project-a-compile</selectedJob>
     */
    void selectedJob(String selectedJob) {
        checkNotNull(selectedJob, 'selectedJob must not be null')

        execute {
            it / methodMissing('selectedJob', selectedJob)
        }
    }

    /**
     * <consoleOutputLinkStyle>Output Style</consoleOutputLinkStyle>
     */
    void consoleOutputLinkStyle(OutputStyle outputStyle) {
        checkNotNull(outputStyle, 'consoleOutputLinkStyle must not be null')

        execute {
            it / methodMissing('consoleOutputLinkStyle', outputStyle.value)
        }
    }

    /**
     * <cssUrl>Css Url</csUrl>
     */
    void customCssUrl(String customCssUrl) {
        execute {
            it / cssUrl(customCssUrl ?: '')
        }
    }

    /**
     * <triggerOnlyLatestJob>true</triggerOnlyLatestJob>
     */
    void triggerOnlyLatestJob(boolean triggerOnlyLatestJob = true) {
        execute {
            it / methodMissing('triggerOnlyLatestJob', triggerOnlyLatestJob)
        }
    }

    /**
     * <alwaysAllowManualTrigger>true</alwaysAllowManualTrigger>
     */
    void alwaysAllowManualTrigger(boolean alwaysAllowManualTrigger = true) {
        execute {
            it / methodMissing('alwaysAllowManualTrigger', alwaysAllowManualTrigger)
        }
    }

    /**
     * <showPipelineParameters>true</showPipelineParameters>
     */
    void showPipelineParameters(boolean showPipelineParameters = true) {
        execute {
            it / methodMissing('showPipelineParameters', showPipelineParameters)
        }
    }

    /**
     * <showPipelineParametersInHeaders>true</showPipelineParametersInHeaders>
     */
    void showPipelineParametersInHeaders(boolean showPipelineParametersInHeaders = true) {
        execute {
            it / methodMissing('showPipelineParametersInHeaders', showPipelineParametersInHeaders)
        }
    }

    /**
     * <refreshFrequency>60</refreshFrequency>
     */
    void refreshFrequency(int refreshFrequency) {
        checkArgument(refreshFrequency > 0, 'refreshFrequency must be greater than zero')

        execute {
            it / methodMissing('refreshFrequency', refreshFrequency)
        }
    }

    /**
     * <showPipelineDefinitionHeader>true</showPipelineDefinitionHeader>
     */
    void showPipelineDefinitionHeader(boolean showPipelineDefinitionHeader = true) {
        execute {
            it / methodMissing('showPipelineDefinitionHeader', showPipelineDefinitionHeader)
        }
    }

    /**
     * <startsWithParameters>true</startsWithParameters>
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
