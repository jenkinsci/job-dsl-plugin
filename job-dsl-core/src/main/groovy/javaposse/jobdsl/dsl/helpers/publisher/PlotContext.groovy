package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Context

class PlotContext implements Context {
    private static final List<String> STYLE = [
        'area', 'bar', 'bar3d', 'line', 'line3d', 'stackedArea', 'stackedbar', 'stackedbar3d', 'waterfall'
    ]

    String style = 'line'
    String dataStore
    String group
    List<PlotSeriesContext> dataSeriesList = []

    PlotContext(String dataStore, String group) {
        this.dataStore = dataStore
        this.group = group
    }

    void style(String style) {
        checkArgument(StringYLE.contains(style), "style must be one of ${STYLE.join(', ')}")
        this.style = style
    }

    void propertiesFile(Closure plotSeriesClosure) {
        PlotSeriesContext plotSeriesContext = new PlotSeriesContext('PropertiesSeries', 'properties')
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }
}
