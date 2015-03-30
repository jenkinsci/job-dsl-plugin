package javaposse.jobdsl.dsl.helpers.publisher

class PlotPropSeriesContext extends PlotSeriesContext {
    String label

    PlotPropSeriesContext(String fileName) {
        super(fileName, 'properties', 'PropertiesSeries')
    }

    void label(String label) {
        this.label = label
    }
}
