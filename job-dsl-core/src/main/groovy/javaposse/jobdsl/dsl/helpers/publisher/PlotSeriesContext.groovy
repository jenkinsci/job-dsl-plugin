package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PlotSeriesContext implements Context {
    final String fileName
    String label
    String fileType
    String seriesType

    PlotSeriesContext(String fileName, String fileType, String seriesType) {
        this.fileName = fileName
        this.fileType = fileType
        this.seriesType = seriesType
    }

    void label(String label) {
        this.label = label
    }
}
