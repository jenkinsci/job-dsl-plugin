job('example') {
    triggers {
        dos('@daily') {
            triggerScript('set CAUSE=Build successfully triggered by dostrigger.')
        }
    }
}
