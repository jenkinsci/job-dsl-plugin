package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

abstract class PlotSeriesContext implements Context {
    final String fileName
    final String fileType
    final String seriesType

    protected PlotSeriesContext(String fileName, String fileType, String seriesType) {
        this.fileName = fileName
        this.fileType = fileType
        this.seriesType = seriesType
    }
}
