package javaposse.jobdsl.dsl.helpers.publisher

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class PlotCSVSeriesContext extends PlotSeriesContext {
    String inclusionFlag = 'OFF'
    List<String> exclusionSet = []
    String url
    boolean showTable

    PlotCSVSeriesContext(String fileName) {
        super(fileName, 'csv', 'CSVSeries')
    }

    void url(String url) {
        this.url = url
    }

    void showTable(boolean showTable = true) {
        this.showTable = showTable
    }

    void includeColumns(String... columnNames) {
        includeExcludeColumn('INCLUDE_BY_STRING', columnNames)
    }

    void excludeColumns(String... columnNames) {
        includeExcludeColumn('EXCLUDE_BY_STRING', columnNames)
    }

    void includeColumns(int... columnIndexes) {
        includeExcludeColumn('INCLUDE_BY_COLUMN', columnIndexes as String[])
    }

    void excludeColumns(int... columnIndexes) {
        includeExcludeColumn('EXCLUDE_BY_COLUMN', columnIndexes as String[])
    }

    private void includeExcludeColumn(String inclusionFlag, String... columnNames) {
        checkArgument(
                this.inclusionFlag in ['OFF', inclusionFlag],
                'Unable to mix includeColumns/excludeColumns arguments or columnName/columnIndex types.'
        )

        this.inclusionFlag = inclusionFlag
        this.exclusionSet.addAll(columnNames)
    }
}
