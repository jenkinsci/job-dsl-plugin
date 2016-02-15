job('example') {
    scm {
        perforceP4('p4_credentials') {
            workspace {
                manual('ws_name', '//depot/Tools/build/... //ws_name/build/...')
            }
            configure { node ->
                node / workspace / spec / clobber('true')
            }
        }
    }
}
