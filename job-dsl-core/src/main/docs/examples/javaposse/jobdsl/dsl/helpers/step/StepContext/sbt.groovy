job('example') {
    steps {
        sbt('SBT 0.12.3', 'test', '-Dsbt.log.noformat=true', '-Xmx2G -Xms512M', 'subproject')
    }
}
