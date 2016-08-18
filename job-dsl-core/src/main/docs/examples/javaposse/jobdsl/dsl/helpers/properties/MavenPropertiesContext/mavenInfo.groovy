mavenJob('example-1') {
    properties {
        mavenInfo {
            modulePattern(':my-artifact')
            interestingDependenciesPattern('org.springframework.*:*')
            assignName('name template')
        }
    }
}

mavenJob('example-2') {
    properties {
        mavenInfo {
            modulePattern(':my-artifact')
            interestingDependenciesPattern('org.springframework.*:*')
            assignDescription('some text')
        }
    }
}
