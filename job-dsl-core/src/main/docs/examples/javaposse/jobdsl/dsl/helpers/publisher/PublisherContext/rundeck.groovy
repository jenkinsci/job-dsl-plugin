job('example-1') {
    publishers {
        rundeck('13eba461-179d-40a1-8a08-bafee33fdc12')
    }
}

job('example-2') {
    publishers {
        rundeck('13eba461-179d-40a1-8a08-bafee33fdc12') {
            options(artifact: 'app', env: 'dev')
            option('version', '1.1')
            tag('deploy app to dev')
            nodeFilters(hostname: 'dev(\\d+).company.net')
            nodeFilter('tags', 'www+dev')
            shouldWaitForRundeckJob()
            shouldFailTheBuild()
            includeRundeckLogs()
        }
    }
}
