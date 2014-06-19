package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PlotPluginContext implements Context {
    def plots = []

    void area(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('area', dataStore, group, plotPluginPlotClosure)
    }

    private void processPlotClosure(String style, String dataStore, String group, Closure plotPluginPlotClosure) {
        PlotPluginPlotContext plotPluginPlotContext = new PlotPluginPlotContext(style, dataStore, group)
        AbstractContextHelper.executeInContext(plotPluginPlotClosure, plotPluginPlotContext)

        plots << plotPluginPlotContext
    }
}
