matrixJob('example') {
    axes {
        label('label', 'linux', 'windows')
        jdk('jdk8', 'jdk11')
        configure { axes ->
            axes << 'org.acme.FooAxis'()
        }
    }
}
