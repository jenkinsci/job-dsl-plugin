package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PlotSeriesContext implements Context {
    final String fileName
    final String fileType
    final String seriesType

    PlotSeriesContext(String fileName, String fileType, String seriesType) {
        this.fileName = fileName
        this.fileType = fileType
        this.seriesType = seriesType
    }
}
