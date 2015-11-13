job('example1') {
    properties {
        scannerJobProperty()
    }
}

job('example2') {
    properties {
        scannerJobProperty(false)
    }
}
