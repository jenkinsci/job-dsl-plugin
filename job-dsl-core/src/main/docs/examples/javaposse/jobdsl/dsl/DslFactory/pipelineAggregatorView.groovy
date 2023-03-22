pipelineAggregatorView('pipeline-aggregator-view') {
    filterRegex('.*Pipeline')
    onlyLastBuild()
    useCondensedTables()
}
