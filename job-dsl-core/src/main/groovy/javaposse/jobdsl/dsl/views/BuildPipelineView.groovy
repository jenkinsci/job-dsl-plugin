package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class BuildPipelineView extends View {
    BuildPipelineView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Sets number of displayed builds. Defaults to 1 and must be greater than zero.
     */
    void displayedBuilds(int displayedBuilds) {
        checkArgument(displayedBuilds > 0, 'displayedBuilds must be greater than zero')

        configure {
            it / noOfDisplayedBuilds(displayedBuilds)
        }
    }

    /**
     * Sets a title for the pipeline.
     */
    void title(String title) {
        configure {
            it / buildViewTitle(title ?: '')
        }
    }

    /**
     * Defines the first job in the pipeline.
     */
    void selectedJob(String selectedJob) {
        checkNotNull(selectedJob, 'selectedJob must not be null')

        configure {
            it / methodMissing('selectedJob', selectedJob)
            if (jobManagement.isMinimumPluginVersionInstalled('build-pipeline-plugin', '1.3.4')) {
                it / gridBuilder(
                        class: 'au.com.centrumsystems.hudson.plugin.buildpipeline.DownstreamProjectGridBuilder',
                ) {
                    firstJob(selectedJob)
                }
            }
        }
    }

    /**
     * Defines the console output style. Defaults to {@code OutputStyle.Lightbox}.
     */
    void consoleOutputLinkStyle(OutputStyle outputStyle) {
        checkNotNull(outputStyle, 'consoleOutputLinkStyle must not be null')

        configure {
            it / methodMissing('consoleOutputLinkStyle', outputStyle.value)
        }
    }

    /**
     * Sets a URL for custom CSS files.
     */
    void customCssUrl(String customCssUrl) {
        configure {
            it / cssUrl(customCssUrl ?: '')
        }
    }

    /**
     * Use this method to restrict the display of a trigger button to only the most recent successful build pipelines.
     * This option will also limit retries to just unsuccessful builds of the most recent build pipelines. Defaults to
     * {@code false}.
     */
    void triggerOnlyLatestJob(boolean triggerOnlyLatestJob = true) {
        configure {
            it / methodMissing('triggerOnlyLatestJob', triggerOnlyLatestJob)
        }
    }

    /**
     * Use this method if you want to be able to execute a successful pipeline step again. Defaults to {@code false}.
     */
    void alwaysAllowManualTrigger(boolean alwaysAllowManualTrigger = true) {
        configure {
            it / methodMissing('alwaysAllowManualTrigger', alwaysAllowManualTrigger)
        }
    }

    /**
     * Use this method if you want to display the parameters used to run the first job in each pipeline's revision box.
     * Defaults to {@code false}.
     */
    void showPipelineParameters(boolean showPipelineParameters = true) {
        configure {
            it / methodMissing('showPipelineParameters', showPipelineParameters)
        }
    }

    /**
     * Use this method if you want to display the parameters used to run the latest successful job in the pipeline's
     * project headers. Defaults to {@code false}.
     */
    void showPipelineParametersInHeaders(boolean showPipelineParametersInHeaders = true) {
        configure {
            it / methodMissing('showPipelineParametersInHeaders', showPipelineParametersInHeaders)
        }
    }

    /**
     * Frequency at which the Build Pipeline Plugin updates the build cards in seconds. Defaults to 3.
     */
    void refreshFrequency(int refreshFrequency) {
        checkArgument(refreshFrequency > 0, 'refreshFrequency must be greater than zero')

        configure {
            it / methodMissing('refreshFrequency', refreshFrequency)
        }
    }

    /**
     * Use this method if you want to show the pipeline definition header in the pipeline view. Defaults to
     * {@code false}.
     */
    void showPipelineDefinitionHeader(boolean showPipelineDefinitionHeader = true) {
        configure {
            it / methodMissing('showPipelineDefinitionHeader', showPipelineDefinitionHeader)
        }
    }

    /**
     * Use this method if you want toggle the "Pipeline starts with parameters" option in the pipeline view
     * configuration. Defaults to {@code false}.
     *
     * @since 1.26
     */
    @RequiresPlugin(id = 'build-pipeline-plugin', minimumVersion = '1.4.3')
    void startsWithParameters(boolean startsWithParameters = true) {
        configure {
            it / methodMissing('startsWithParameters', startsWithParameters)
        }
    }

    static enum OutputStyle {
        Lightbox('Lightbox'),
        NewWindow('New Window'),
        ThisWindow('This Window')

        final String value

        OutputStyle(String value) {
            this.value = value
        }
    }
}
