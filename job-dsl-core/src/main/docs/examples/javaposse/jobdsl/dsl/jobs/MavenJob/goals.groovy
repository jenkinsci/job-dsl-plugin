mavenJob('example-1') {
    goals('clean install -DskipTests')
}

mavenJob('example-2') {
    goals('clean')
    goals('install')
    goals('-DskipTests')
}
