job('example') {
    environmentVariables {
        groovy('''
            def a = 1
            return [TWO: 2 * a]
        ''')
    }
}
