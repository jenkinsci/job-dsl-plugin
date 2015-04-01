package javaposse.jobdsl.dsl.helpers.publisher

import static com.google.common.base.Preconditions.checkArgument

class PlotCSVSeriesContext extends PlotSeriesContext {
    String inclusionFlag = 'OFF'
    List <Integer> colExclusionSet = []
    List <String> strExclusionSet = []
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

    void includeColumn(String columnName) {
        String inclusionFlag = 'INCLUDE_BY_STRING'
        checkArgument(['OFF', inclusionFlag].contains(this.inclusionFlag),
            'Unable to mix includeColumns/excludeColumns arguments or columnName/columnIndex types.')

        this.inclusionFlag = inclusionFlag
        this.strExclusionSet.add(columnName)
    }

    void excludeColumn(String columnName) {
        String inclusionFlag = 'EXCLUDE_BY_STRING'
        checkArgument(['OFF', inclusionFlag].contains(this.inclusionFlag),
            'Unable to mix includeColumns/excludeColumns arguments or columnName/columnIndex types.')

        this.inclusionFlag = inclusionFlag
        this.strExclusionSet.add(columnName)
    }

    void includeColumn(Integer columnIndex) {
        String inclusionFlag = 'INCLUDE_BY_COLUMN'
        checkArgument(['OFF', inclusionFlag].contains(this.inclusionFlag),
            'Unable to mix includeColumns/excludeColumns arguments or columnName/columnIndex types.')

        this.inclusionFlag = inclusionFlag
        this.colExclusionSet.add(columnIndex)
    }

    void excludeColumn(Integer columnIndex) {
        String inclusionFlag = 'EXCLUDE_BY_COLUMN'
        checkArgument(['OFF', inclusionFlag].contains(this.inclusionFlag),
            'Unable to mix includeColumns/excludeColumns arguments or columnName/columnIndex types.')

        this.inclusionFlag = inclusionFlag
        this.colExclusionSet.add(columnIndex)
    }
}
