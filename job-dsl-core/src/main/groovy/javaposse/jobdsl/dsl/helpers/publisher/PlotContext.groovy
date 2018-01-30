package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class PlotContext extends AbstractContext {
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
    Double yAxisMinimum
    Double yAxisMaximum

    PlotContext(JobManagement jobManagement, String group, String dataStore) {
        super(jobManagement)
        this.group = group
        this.dataStore = dataStore
    }

    /**
     * Specifies the plot title.
     */
    void title(String title) {
        this.title = title
    }

    /**
     * Specifies the plot's y-axis label.
     */
    void yAxis(String yAxis) {
        this.yAxis = yAxis
    }

    /**
     * Specifies the graph style of the plot. Must be one of {@code 'area'}, {@code 'bar'}, {@code 'bar3d'},
     * {@code 'line'} (default), {@code 'line3d'}, {@code 'stackedArea'}, {@code 'stackedbar'}, {@code 'stackedbar3d'}
     * or {@code 'waterfall'}.
     */
    void style(String style) {
        checkArgument(STYLE.contains(style), "style must be one of ${STYLE.join(', ')}")
        this.style = style
    }

    /**
     * Specifies the number of builds to plot across, starting with the latest build.
     */
    void numberOfBuilds(int numberOfBuilds) {
        this.numberOfBuilds = numberOfBuilds
    }

    /**
     * If set, uses build descriptions used as X-axis labels. Defaults to {@code false}.
     */
    void useDescriptions(boolean useDescriptions = true) {
        this.useDescriptions = useDescriptions
    }

    /**
     * If set, shows all builds up to included number of builds. Defaults to {@code false}.
     */
    void keepRecords(boolean keepRecords = true) {
        this.keepRecords = keepRecords
    }

    /**
     * If set, does not automatically include the value zero. Defaults to {@code false}.
     */
    void excludeZero(boolean excludeZero = true) {
        this.excludeZero = excludeZero
    }

    /**
     * If set, the Y-axis will use a logarithmic scale. Defaults to {@code false}.
     */
    void logarithmic(boolean logarithmic = true) {
        this.logarithmic = logarithmic
    }

    /**
     * Specifies the minimum value for the y-axis.
     *
     * @since 1.68
     */
    @RequiresPlugin(id = 'plot', minimumVersion = '1.10')
    void yAxisMinimum(Double yAxisMinimum) {
        this.yAxisMinimum = yAxisMinimum
    }

    /**
     * Specifies the maximum value for the y-axis.
     *
     * @since 1.68
     */
    @RequiresPlugin(id = 'plot', minimumVersion = '1.10')
    void yAxisMaximum(Double yAxisMaximum) {
        this.yAxisMaximum = yAxisMaximum
    }

    /**
     * Loads a data series from a CSV file. Can be called multiple times to add more data series.
     */
    void csvFile(String fileName, @DslContext(PlotCSVSeriesContext) Closure plotSeriesClosure = null) {
        checkNotNullOrEmpty(fileName, 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotCSVSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }

    /**
     * Loads a data series from a properties file. Can be called multiple times to add more data series.
     */
    void propertiesFile(String fileName, @DslContext(PlotPropertiesSeriesContext) Closure plotSeriesClosure = null) {
        checkNotNullOrEmpty(fileName, 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotPropertiesSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }

    /**
     * Loads a data series from a XML file. Can be called multiple times to add more data series.
     */
    void xmlFile(String fileName, @DslContext(PlotXMLSeriesContext) Closure plotSeriesClosure = null) {
        checkNotNullOrEmpty(fileName, 'fileName must not be null or empty')

        PlotSeriesContext plotSeriesContext = new PlotXMLSeriesContext(fileName)
        ContextHelper.executeInContext(plotSeriesClosure, plotSeriesContext)

        dataSeriesList << plotSeriesContext
    }
}
