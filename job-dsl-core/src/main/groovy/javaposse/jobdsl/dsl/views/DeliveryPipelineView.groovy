package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class DeliveryPipelineView extends View {
    DeliveryPipelineView(JobManagement jobManagement) {
        super(jobManagement)
    }

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

    void showTotalBuildTime(boolean value = true) {
        execute {
            it / methodMissing('showTotalBuildTime', value)
        }
    }

    void allowRebuild(boolean value = true) {
        execute {
            it / methodMissing('allowRebuild', value)
        }
    }

    void allowPipelineStart(boolean value = true) {
        execute {
            it / methodMissing('allowPipelineStart', value)
        }
    }

    void showDescription(boolean value = true) {
        execute {
            it / methodMissing('showDescription', value)
        }
    }

    void showPromotions(boolean value = true) {
        execute {
            it / methodMissing('showPromotions', value)
        }
    }

    void pipelines(@DslContext(DeliveryPipelinesContext) Closure pipelinesClosure) {
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
