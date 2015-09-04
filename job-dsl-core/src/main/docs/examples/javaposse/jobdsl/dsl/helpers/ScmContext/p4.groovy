job('example') {
    scm {
        p4('//depot/Tools/build') { node ->
            node / p4Port('perforce:1666')
            node / p4Tool('/usr/bin/p4')
            node / exposeP4Passwd('false')
            node / pollOnlyOnMaster('true')
        }
    }
}
