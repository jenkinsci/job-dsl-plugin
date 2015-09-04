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

    /**
     * If set, opens the URL when a point is clicked.
     */
    void url(String url) {
        this.url = url
    }

    /**
     * If set, the original CSV data will be shown above the plot.
     */
    void showTable(boolean showTable = true) {
        this.showTable = showTable
    }

    /**
     * Specifies the column names to include in the plot.
     */
    void includeColumns(String... columnNames) {
        includeExcludeColumn('INCLUDE_BY_STRING', columnNames)
    }

    /**
     * Specifies the column names to exclude from the plot.
     */
    void excludeColumns(String... columnNames) {
        includeExcludeColumn('EXCLUDE_BY_STRING', columnNames)
    }

    /**
     * Specifies the column indexes to include in the plot.
     */
    void includeColumns(int... columnIndexes) {
        includeExcludeColumn('INCLUDE_BY_COLUMN', columnIndexes as String[])
    }

    /**
     * Specifies the column indexes to exclude from the plot.
     */
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
