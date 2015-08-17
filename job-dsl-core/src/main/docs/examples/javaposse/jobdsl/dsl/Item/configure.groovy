job('example-1') {
    configure { project ->
        project / 'properties' / 'com.example.Test' {
            'switch'('on')
        }
    }
}

folder('example-2') {
    configure { folder ->
        folder / icon(class: 'org.example.MyFolderIcon')
    }
}
