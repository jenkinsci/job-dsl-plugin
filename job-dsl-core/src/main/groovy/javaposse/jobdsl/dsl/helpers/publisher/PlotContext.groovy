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
    String title = ''
    String yaxis = ''
    String style = 'line'
    int numBuilds
    boolean useDescr = false
    boolean keepRecords = false
    boolean exclZero = false

    PlotContext(String group, String dataStore) {
        this.group = group
        this.dataStore = dataStore
    }

    void title(String title) {
        this.title = title
    }

    void yaxis(String yaxis) {
        this.yaxis = yaxis
    }

    void style(String style) {
        checkArgument(STYLE.contains(style), "style must be one of ${STYLE.join(', ')}")
        this.style = style
    }

    void numBuilds(int numBuilds) {
        this.numBuilds = numBuilds
    }

    void useDescr(boolean useDescr = true) {
        this.useDescr = useDescr
    }

    void keepRecords(boolean keepRecords = true) {
        this.keepRecords = keepRecords
    }

    void exclZero(boolean exclZero = true) {
        this.exclZero = exclZero
    }

    void propertiesFile(String fileName, Closure plotSeriesClosure = null) {
        checkArgument(!Strings.isNullOrEmpty(fileName), 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }
}
