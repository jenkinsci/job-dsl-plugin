package javaposse.jobdsl.dsl.helpers.publisher

class PlotPropertiesSeriesContext extends PlotSeriesContext {
    String label

    PlotPropertiesSeriesContext(String fileName) {
        super(fileName, 'properties', 'PropertiesSeries')
    }

    /**
     * Specifies the legend label for this data series.
     */
    void label(String label) {
        this.label = label
    }
}
