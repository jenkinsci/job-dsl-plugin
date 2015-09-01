job('example') {
    steps {
        buildDescription(/.*\[INFO\] Uploading project information for [^\s]* ([^\s]*)/)
    }
}
