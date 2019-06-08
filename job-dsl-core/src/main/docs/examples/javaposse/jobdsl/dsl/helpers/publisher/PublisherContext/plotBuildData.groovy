job('example-1') {
    publishers {
        plotBuildData {
            plot('Important Plot', 'my_data_store.csv') {
                propertiesFile('my_data.properties')
            }
        }
    }
}

job('example-2') {
    publishers {
        plotBuildData {
            plot('Bar Charts', 'bar_chart_data_store.csv') {
                style('bar')
                propertiesFile('my_data.properties') {
                    label('My Label')
                }
            }
        }
    }
}

job('example-3') {
    publishers {
        plotBuildData {
            plot('Exciting plots', 'excitement.csv') {
                title('X vs Y')
                yAxis('Y')
                numberOfBuilds(42)
                useDescriptions()
                keepRecords()
                excludeZero()
                logarithmic()
                yAxisMinimum(10.0)
                yAxisMaximum(100.0)
                propertiesFile('my_data.properties') {
                    label('Builds')
                }
            }
        }
    }
}

job('example-4') {
    publishers {
        plotBuildData {
            plot('Other charts', '123012992213.csv') {
                style('line3d')
                csvFile('my_data.properties') {
                    includeColumns(1, 8, 14)
                    showTable()
                }
            }
        }
    }
}
