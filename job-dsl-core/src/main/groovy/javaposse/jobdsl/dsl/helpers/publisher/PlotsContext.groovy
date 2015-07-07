package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Preconditions

class PlotsContext implements Context {
    final List<PlotContext> plots = []

    void plot(String group, String dataStore, @DslContext(PlotContext) Closure plotClosure) {
        Preconditions.checkNotNullOrEmpty(group, 'group must not be null or empty')
        Preconditions.checkNotNullOrEmpty(dataStore, 'dataStore must not be null or empty')

        PlotContext plotContext = new PlotContext(group, dataStore)
        ContextHelper.executeInContext(plotClosure, plotContext)

        plots << plotContext
    }
}
