package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class PlotSeriesContext implements Context {
    final String fileName
    String label

    PlotSeriesContext(String fileName) {
        this.fileName = fileName
    }

    void label(String label) {
        this.label = label
    }
}
