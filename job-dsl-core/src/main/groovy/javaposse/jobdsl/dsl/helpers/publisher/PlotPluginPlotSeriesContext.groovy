package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class PlotPluginPlotSeriesContext implements Context {
    final String type
    final String fileType
    String file = ''
    String label = ''

    PlotPluginPlotSeriesContext(String type, String fileType) {
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
