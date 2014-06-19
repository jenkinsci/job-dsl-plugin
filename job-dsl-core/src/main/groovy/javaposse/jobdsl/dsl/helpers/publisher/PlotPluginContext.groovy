package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PlotPluginContext implements Context {
    def plots = []

    void area(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('area', dataStore, group, plotPluginPlotClosure)
    }

    void bar(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('bar', dataStore, group, plotPluginPlotClosure)
    }

    void bar3d(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('bar3d', dataStore, group, plotPluginPlotClosure)
    }

    void line(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('line', dataStore, group, plotPluginPlotClosure)
    }

    void line3d(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('line3d', dataStore, group, plotPluginPlotClosure)
    }

    void stackedArea(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('stackedArea', dataStore, group, plotPluginPlotClosure)
    }

    void stackedbar(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('stackedbar', dataStore, group, plotPluginPlotClosure)
    }

    void stackedbar3d(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('stackedbar3d', dataStore, group, plotPluginPlotClosure)
    }

    void waterfall(String dataStore, String group, Closure plotPluginPlotClosure) {
        processPlotClosure('waterfall', dataStore, group, plotPluginPlotClosure)
    }

    private void processPlotClosure(String style, String dataStore, String group, Closure plotPluginPlotClosure) {
        PlotPluginPlotContext plotPluginPlotContext = new PlotPluginPlotContext(style, dataStore, group)
        AbstractContextHelper.executeInContext(plotPluginPlotClosure, plotPluginPlotContext)

        plots << plotPluginPlotContext
    }
}
