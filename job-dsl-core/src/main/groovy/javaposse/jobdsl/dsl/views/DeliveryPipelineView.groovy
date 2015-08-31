package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class DeliveryPipelineView extends View {
    DeliveryPipelineView(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    @NoDoc
    void description(String description) {
        super.description(description)
    }

    @Override
    @NoDoc
    void filterBuildQueue(boolean filterBuildQueue = true) {
        super.filterBuildQueue(filterBuildQueue)
    }

    @Override
    @NoDoc
    void filterExecutors(boolean filterExecutors = true) {
        super.filterExecutors(filterExecutors)
    }

    /**
     * Sets the number of pipelines instances shown for each pipeline. Defaults to 3.
     */
    void pipelineInstances(int number) {
        execute {
            it / noOfPipelines(number)
        }
    }

    /**
     * Show a aggregated view where each stage shows the latest version being executed. Defaults to {@code false}.
     */
    void showAggregatedPipeline(boolean value = true) {
        execute {
            it / methodMissing('showAggregatedPipeline', value)
        }
    }

    /**
     * Sets the number of columns used for showing pipelines. Defaults to 1.
     */
    void columns(int number) {
        execute {
            it / noOfColumns(number)
        }
    }

    /**
     * Specifies how to sort the pipeline in the view, only applicable for several pipelines. Defaults to
     * {@code Sorting.NONE}.
     */
    void sorting(Sorting sorting) {
        execute {
            it / methodMissing('sorting', (sorting ?: Sorting.NONE).value)
        }
    }

    /**
     * Show avatar pictures instead of user names. Defaults to {@code false}.
     */
    void showAvatars(boolean value = true) {
        execute {
            it / methodMissing('showAvatars', value)
        }
    }

    /**
     * Specifies how often the view will be updated. Defaults to 2.
     */
    void updateInterval(int seconds) {
        execute {
            it / methodMissing('updateInterval', seconds)
        }
    }

    /**
     * Show SCM change log for the first job in the pipeline. Defaults to {@code false}.
     */
    void showChangeLog(boolean value = true) {
        execute {
            it / methodMissing('showChanges', value)
        }
    }

    /**
     * Show a button if a task is manual. Defaults to {@code false}.
     */
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

    /**
     * Defines pipelines by either specifying names and start jobs or by regular expressions. Both variants can be
     * called multiple times to add different pipelines to the view.
     */
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
