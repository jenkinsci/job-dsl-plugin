// use job name as task name
job('example-1') {
    deliveryPipelineConfiguration('qa')
}

// use custom task name
job('example-2') {
    deliveryPipelineConfiguration('qa', 'integration-tests')
}
