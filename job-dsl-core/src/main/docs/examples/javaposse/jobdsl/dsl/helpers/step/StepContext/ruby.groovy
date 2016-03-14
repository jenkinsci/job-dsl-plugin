job('example') {
    steps {
        ruby("puts 'Hello'")
        ruby(readFileFromWorkspace('build.rb'))
    }
}
