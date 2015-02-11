package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Context

class PlotPluginContext implements Context {
    List<PlotPluginPlotContext> plots = []

    void line(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('line', dataStore, group, plotPluginPlotClosure)
    }

    private void processPlotClosure(String style, String dataStore, String group, Closure plotPluginPlotClosure) {
        PlotPluginPlotContext plotPluginPlotContext = new PlotPluginPlotContext(style, dataStore, group)
        ContextHelper.executeInContext(plotPluginPlotClosure, plotPluginPlotContext)

        plots << plotPluginPlotContext
    }
}
