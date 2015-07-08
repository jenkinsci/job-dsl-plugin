package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class PlotContext implements Context {
    private static final List<String> STYLE = [
        'area', 'bar', 'bar3d', 'line', 'line3d', 'stackedArea', 'stackedbar', 'stackedbar3d', 'waterfall'
    ]

    final String group
    final String dataStore
    final List<PlotSeriesContext> dataSeriesList = []
    String title
    String yAxis
    String style = 'line'
    Integer numberOfBuilds
    boolean useDescriptions
    boolean keepRecords
    boolean excludeZero
    boolean logarithmic

    PlotContext(String group, String dataStore) {
        this.group = group
        this.dataStore = dataStore
    }

    void title(String title) {
        this.title = title
    }

    void yAxis(String yAxis) {
        this.yAxis = yAxis
    }

    void style(String style) {
        checkArgument(STYLE.contains(style), "style must be one of ${STYLE.join(', ')}")
        this.style = style
    }

    void numberOfBuilds(int numberOfBuilds) {
        this.numberOfBuilds = numberOfBuilds
    }

    void useDescriptions(boolean useDescriptions = true) {
        this.useDescriptions = useDescriptions
    }

    void keepRecords(boolean keepRecords = true) {
        this.keepRecords = keepRecords
    }

    void excludeZero(boolean excludeZero = true) {
        this.excludeZero = excludeZero
    }

    void logarithmic(boolean logarithmic = true) {
        this.logarithmic = logarithmic
    }

    void csvFile(String fileName, @DslContext(PlotCSVSeriesContext) Closure plotSeriesClosure = null) {
        checkNotNullOrEmpty(fileName, 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotCSVSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }

    void propertiesFile(String fileName, @DslContext(PlotPropertiesSeriesContext) Closure plotSeriesClosure = null) {
        checkNotNullOrEmpty(fileName, 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotPropertiesSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }

    void xmlFile(String fileName, @DslContext(PlotXMLSeriesContext) Closure plotSeriesClosure = null) {
        checkNotNullOrEmpty(fileName, 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotXMLSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }
}
