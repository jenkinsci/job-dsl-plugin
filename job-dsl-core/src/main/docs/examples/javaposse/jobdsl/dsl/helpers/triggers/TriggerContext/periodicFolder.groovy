job('example-1') {
    triggers {
        periodicFolderTrigger {
            spec('* * * * *')
            interval(60000)
        }
    }
}
