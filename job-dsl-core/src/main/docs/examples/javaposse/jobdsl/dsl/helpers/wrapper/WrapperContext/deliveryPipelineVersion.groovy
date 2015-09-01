job('example') {
    wrappers {
        deliveryPipelineVersion('1.0.${BUILD_NUMBER}', true)
    }
}
