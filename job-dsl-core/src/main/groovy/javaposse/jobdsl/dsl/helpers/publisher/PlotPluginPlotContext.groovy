package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Context

class PlotPluginPlotContext implements Context {
    String style
    String dataStore
    String group
    List<PlotPluginPlotSeriesContext> dataSeriesList = []

    PlotPluginPlotContext(String style, String dataStore, String group) {
        this.style = style
        this.dataStore = dataStore
        this.group = group
    }

    void propertiesFile(Closure plotPluginPlotSeriesClosure) {
        PlotPluginPlotSeriesContext plotPluginPlotSeriesContext =
            new PlotPluginPlotSeriesContext('PropertiesSeries', 'properties')
        ContextHelper.executeInContext(plotPluginPlotSeriesClosure, plotPluginPlotSeriesContext)

        dataSeriesList << plotPluginPlotSeriesContext
    }
}
