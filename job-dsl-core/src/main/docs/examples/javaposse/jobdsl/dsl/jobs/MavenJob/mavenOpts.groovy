mavenJob('example-1') {
    mavenOpts('-Xmx1536m -XX:MaxPermSize=384m -XX:ReservedCodeCacheSize=64m')
}

mavenJob('example-2') {
    mavenOpts('-Xmx1536m')
    mavenOpts('-XX:MaxPermSize=384m')
    mavenOpts('-XX:ReservedCodeCacheSize=64m')
}
