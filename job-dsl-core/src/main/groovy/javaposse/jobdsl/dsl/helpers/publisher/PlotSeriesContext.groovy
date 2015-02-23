package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PlotSeriesContext implements Context {
    final String type
    final String fileType
    String file = ''
    String label = ''

    PlotSeriesContext(String type, String fileType) {
        this.type = type
        this.fileType = fileType
    }

    void file(String file) {
        this.file = file
    }

    void label(String label) {
        this.label = label
    }
}
