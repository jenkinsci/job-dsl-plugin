matrixJob('example') {
    axes {
        label('label', 'linux', 'windows')
        jdk('jdk6', 'jdk7')
        configure { axes ->
            axes << 'org.acme.FooAxis'()
        }
    }
}
