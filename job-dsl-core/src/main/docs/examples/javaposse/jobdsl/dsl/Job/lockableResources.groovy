// lock single resource
job('example-1') {
    lockableResources('lock-resource')
}

// notation that locks three resources at once
job('example-2') {
    lockableResources('resource1 resource2 resource3')
}

// lock two available resources from given three and capture locked resources in the variable name
job('example-3') {
    lockableResources('resource1 resource2 resource3') {
        resourcesVariable('LOCKED_RESOURCES')
        resourceNumber(2)
    }
    steps {
        shell('echo Following resources are locked: $LOCKED_RESOURCES')
    }
}

// lock resource by label
job('example-4') {
    lockableResources {
        label('heavy_resource')
        resourceNumber(1)
    }
}
