deliveryPipelineView('example') {
    pipelines {
        component('Sub System A', 'compile-a')
        component('Sub System B', 'compile-b')
    }
}
