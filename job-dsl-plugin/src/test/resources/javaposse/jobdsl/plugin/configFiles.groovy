configFiles {
    customConfig {
        id('one')
        name('Config 1')
        comment('lorem')
        content('ipsum')
        providerId('???')
    }
    powerShellConfig {
        id('two')
        name('Config 2')
        comment('foo')
        content('bar')
        args {
            arg {
                name('arg1')
            }
        }
    }
}
