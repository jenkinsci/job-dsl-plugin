job('example-1') {
    scm {
        perforcep4('p4_credentials') {
            manual('ws_name', '//depot/Tools/build/... //ws_name/build/...')
        }
    }
}

job('example-2') {
    scm {
        perforcep4('p4_credentials') {
            manual('ws_name', '//depot/Tools/build/... //ws_name/build/...')
            configure { node ->
                node / workspace / spec / clobber('true')
            }
        }
    }
}
