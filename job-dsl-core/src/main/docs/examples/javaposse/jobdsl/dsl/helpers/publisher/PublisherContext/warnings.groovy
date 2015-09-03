job('example') {
    publishers {
        warnings(['Java Compiler (javac)'], ['Java Compiler (javac)': '**/*.log']) {
            excludePattern('**/test**')
            resolveRelativePaths()
        }
    }
}
