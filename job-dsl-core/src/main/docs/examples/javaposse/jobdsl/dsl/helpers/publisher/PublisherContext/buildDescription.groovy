// sets build description to the project version in case the output contains the line "Building my.project.name 0.4.0"
job('example-1') {
    publishers {
        buildDescription(/.*Building [^\s]* ([^\s]*)/)
    }
}

//sets the build description to a values defined by a build parameter or environment variable called BRANCH
job('example-2') {
    publishers {
        buildDescription('', '${BRANCH}')
    }
}
