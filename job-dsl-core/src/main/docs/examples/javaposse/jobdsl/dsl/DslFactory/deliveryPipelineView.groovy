deliveryPipelineView('project-a') {
    pipelineInstances(5)
    showAggregatedPipeline()
    columns(2)
    sorting(Sorting.TITLE)
    updateInterval(60)
    enableManualTriggers()
    showAvatars()
    showChangeLog()
    pipelines {
        component('Sub System A', 'compile-a')
        component('Sub System B', 'compile-b')
        regex(/compile-subsystem-(.*)/)
    }
}
