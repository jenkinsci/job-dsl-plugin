package javaposse.jobdsl.dsl.helpers.publisher

class PlotPropertiesSeriesContext extends PlotSeriesContext {
    String label

    PlotPropertiesSeriesContext(String fileName) {
        super(fileName, 'properties', 'PropertiesSeries')
    }

    void label(String label) {
        this.label = label
    }
}
