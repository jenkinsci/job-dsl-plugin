job('example-1') {
    configure { project ->
        project / 'properties' / 'com.example.Test' {
            'switch'('on')
        }
    }
}
