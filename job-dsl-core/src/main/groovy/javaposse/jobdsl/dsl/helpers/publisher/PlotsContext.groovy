package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Context

class PlotsContext implements Context {
    List<PlotContext> plots = []

    void plot(String dataStore, String group, Closure plotClosure) {
        PlotContext plotContext = new PlotContext(dataStore, group)
        ContextHelper.executeInContext(plotClosure, plotContext)

        plots << plotContext
    }
}
