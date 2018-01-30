package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

class PlotsContext extends AbstractContext {
    final List<PlotContext> plots = []

    PlotsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Adds a plot containing one or more data series. Can be called multiple times to add more plots.
     *
     * Plot plugin relies on a data store to hold the plot data, this is normally stored in a randomly named CSV file
     * within the workspace root. To avoid conflicts this location needs to be set manually relative to the workspace
     * using the {@code dataStore} parameter.
     */
    void plot(String group, String dataStore, @DslContext(PlotContext) Closure plotClosure) {
        Preconditions.checkNotNullOrEmpty(group, 'group must not be null or empty')
        Preconditions.checkNotNullOrEmpty(dataStore, 'dataStore must not be null or empty')

        PlotContext plotContext = new PlotContext(jobManagement, group, dataStore)
        ContextHelper.executeInContext(plotClosure, plotContext)

        plots << plotContext
    }
}
