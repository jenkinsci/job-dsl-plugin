// execute echo command
job('example-1') {
    steps {
        shell('echo Hello World!')
    }
}

// read file from workspace
job('example-2') {
    steps {
        shell(readFileFromWorkspace('build.sh'))
    }
}

// mark the build as unstable instead of failed when the script exits with 3
job('example-3') {
    steps {
        shell('./run-tests.sh') {
            unstableReturn(3)
        }
    }
}
