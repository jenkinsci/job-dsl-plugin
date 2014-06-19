package javaposse.jobdsl.dsl.helpers.publisher

class PlotPluginPlotCSVContext extends PlotPluginPlotSeriesContext {
    String url = ''
    InclusionFlag inclusionFlag = InclusionFlag.OFF
    String exclusionValues =''
    boolean showTable = false

    PlotPluginPlotCSVContext() {
        super('CSVSeries', 'csv')
    }

    void url(String url) {
        this.url = url
    }

    void inclusionFlag(InclusionFlag inclusionFlag) {
        this.inclusionFlag = inclusionFlag
    }

    void exclusionValues(String exclusionValues) {
        this.exclusionValues = exclusionValues
    }

    void showTable(boolean showTable = true) {
        this.showTable = showTable
    }

    static enum InclusionFlag {
        OFF, INCLUDE_BY_STRING, EXCLUDE_BY_STRING, INCLUDE_BY_COLUMN, EXCLUDE_BY_COLUMN
    }
}
