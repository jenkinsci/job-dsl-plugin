package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PlotPluginPlotContext implements Context {
    String style
    String dataStore
    String group
    String title = ''
    int numBuilds
    String yAxis = ''
    boolean useBuildDescr = false
    def dataSeriesList = []

    PlotPluginPlotContext(String style, String dataStore, String group) {
        this.style = style
        this.dataStore = dataStore
        this.group = group
    }

    void title(String title) {
        this.title = title
    }

    void numBuilds(int numBuilds) {
        this.numBuilds = numBuilds
    }

    void yAxis(String yAxis) {
        this.yAxis = yAxis
    }

    void useBuildDescr(boolean useBuildDescr = true) {
        this.useBuildDescr = useBuildDescr
    }

    void propertiesFile(Closure plotPluginPlotSeriesClosure) {
        PlotPluginPlotSeriesContext plotPluginPlotSeriesContext =
            new PlotPluginPlotSeriesContext('PropertiesSeries', 'properties')
        AbstractContextHelper.executeInContext(plotPluginPlotSeriesClosure, plotPluginPlotSeriesContext)

        dataSeriesList << plotPluginPlotSeriesClosure
    }
}
