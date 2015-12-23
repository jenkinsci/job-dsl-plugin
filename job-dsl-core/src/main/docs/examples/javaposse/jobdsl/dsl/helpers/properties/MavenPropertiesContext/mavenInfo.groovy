mavenJob('example_1') {
    properties {
        mavenInfo {
            modulePattern(':my-artifact')
            interestingDependenciesPattern('org.springframework.*:*')
            assignName('name template')
        }
    }
}

mavenJob('example_2') {
    properties {
        mavenInfo {
            modulePattern(':my-artifact')
            interestingDependenciesPattern('org.springframework.*:*')
            assignDescription('some text')
        }
    }
}
