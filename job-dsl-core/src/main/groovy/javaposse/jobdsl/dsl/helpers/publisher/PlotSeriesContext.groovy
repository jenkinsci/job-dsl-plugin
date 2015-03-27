package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PlotSeriesContext implements Context {
    final String fileName
    final String fileType
    final String seriesType
    String label

    PlotSeriesContext(String fileName, String fileType, String seriesType) {
        this.fileName = fileName
        this.fileType = fileType
        this.seriesType = seriesType
    }

    void label(String label) {
        this.label = label
    }
}
