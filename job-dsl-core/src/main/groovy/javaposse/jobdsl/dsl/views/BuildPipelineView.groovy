package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.View

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

class BuildPipelineView extends View {
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
     * <consoleOutputLinkStyle>Lightbox</consoleOutputLinkStyle>
     */
    void consoleOutputLinkStyle(OutputStyle outputStyle = OutputStyle.Lightbox) {
        checkNotNull(outputStyle, 'consoleOutputLinkStyle must not be null')

        execute {
            it / methodMissing('consoleOutputLinkStyle', outputStyle.value)
        }
    }

    /**
     * <cssUrl>Custom URL for CSS files</csUrl>
     */
    void css(String css) {
        execute {
            it / cssUrl(css ?: '')
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
            it / methodMissing('showPipelineParametersInHeaders' ,showPipelineParametersInHeaders)
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

    @Override
    protected String getTemplate() {
        return '''<?xml version='1.0' encoding='UTF-8'?>
<au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <noOfDisplayedBuilds>1</noOfDisplayedBuilds>
    <buildViewTitle/>
    <consoleOutputLinkStyle>Lightbox</consoleOutputLinkStyle>
    <cssUrl/>
    <triggerOnlyLatestJob>false</triggerOnlyLatestJob>
    <alwaysAllowManualTrigger>false</alwaysAllowManualTrigger>
    <showPipelineParameters>false</showPipelineParameters>
    <showPipelineParametersInHeaders>false</showPipelineParametersInHeaders>
    <refreshFrequency>3</refreshFrequency>
    <showPipelineDefinitionHeader>false</showPipelineDefinitionHeader>
</au.com.centrumsystems.hudson.plugin.buildpipeline.BuildPipelineView>'''
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
