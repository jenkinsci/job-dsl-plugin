package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Strings
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Context

import static com.google.common.base.Preconditions.checkArgument

class PlotContext implements Context {
    private static final List<String> STYLE = [
        'area', 'bar', 'bar3d', 'line', 'line3d', 'stackedArea', 'stackedbar', 'stackedbar3d', 'waterfall'
    ]

    final String group
    final String dataStore
    final List<PlotSeriesContext> dataSeriesList = []
    String style = 'line'

    PlotContext(String group, String dataStore) {
        this.group = group
        this.dataStore = dataStore
    }

    void style(String style) {
        checkArgument(STYLE.contains(style), "style must be one of ${STYLE.join(', ')}")
        this.style = style
    }

    void propertiesFile(String fileName, Closure plotSeriesClosure = null) {
        checkArgument(!Strings.isNullOrEmpty(fileName), 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }
}
