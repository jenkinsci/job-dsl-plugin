package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PlotSeriesContext implements Context {
    final String type
    final String fileType
    String file = ''
    String label = ''

    PlotSeriesContext(String fileName, String type, String fileType) {
        this.file = fileName
        this.type = type
        this.fileType = fileType
    }

    void label(String label) {
        this.label = label
    }
}
