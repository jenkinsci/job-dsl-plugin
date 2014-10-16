package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.helpers.ContextHelper.executeInContext

class DeliveryPipelineView extends View {
    void pipelineInstances(int number) {
        execute {
            it / noOfPipelines(number)
        }
    }

    void showAggregatedPipeline(boolean value = true) {
        execute {
            it / methodMissing('showAggregatedPipeline', value)
        }
    }

    void columns(int number) {
        execute {
            it / noOfColumns(number)
        }
    }

    void sorting(Sorting sorting) {
        execute {
            it / methodMissing('sorting', (sorting ?: Sorting.NONE).value)
        }
    }

    void showAvatars(boolean value = true) {
        execute {
            it / methodMissing('showAvatars', value)
        }
    }

    void updateInterval(int seconds) {
        execute {
            it / methodMissing('updateInterval', seconds)
        }
    }

    void showChangeLog(boolean value = true) {
        execute {
            it / methodMissing('showChanges', value)
        }
    }

    void enableManualTriggers(boolean value = true) {
        execute {
            it / methodMissing('allowManualTriggers', value)
        }
    }

    void pipelines(Closure pipelinesClosure) {
        DeliveryPipelinesContext context = new DeliveryPipelinesContext()
        executeInContext(pipelinesClosure, context)

        execute {
            context.components.each { String name, String firstJob ->
                it / 'componentSpecs' << 'se.diabol.jenkins.pipeline.DeliveryPipelineView_-ComponentSpec' {
                    delegate.name(name)
                    delegate.firstJob(firstJob)
                }
            }
            context.regularExpressions.each { String regexp ->
                it / 'regexpFirstJobs' << 'se.diabol.jenkins.pipeline.DeliveryPipelineView_-RegExpSpec' {
                    delegate.regexp(regexp)
                }
            }
        }
    }

    @Override
    protected String getTemplate() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<se.diabol.jenkins.pipeline.DeliveryPipelineView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <noOfPipelines>3</noOfPipelines>
    <showAggregatedPipeline>false</showAggregatedPipeline>
    <noOfColumns>1</noOfColumns>
    <sorting>none</sorting>
    <showAvatars>false</showAvatars>
    <updateInterval>2</updateInterval>
    <showChanges>false</showChanges>
    <allowManualTriggers>false</allowManualTriggers>
</se.diabol.jenkins.pipeline.DeliveryPipelineView>'''
    }

    static enum Sorting {
        NONE('none'),
        TITLE('se.diabol.jenkins.pipeline.sort.NameComparator'),
        LAST_ACTIVITY('se.diabol.jenkins.pipeline.sort.LatestActivityComparator')

        final String value

        Sorting(String value) {
            this.value = value
        }
    }
}
