job('example1') {
    publishers {
        consoleParsing {
            globalRules('/var/lib/jenkins/userContent/logparser/unitybuilder.parser')
            unstableOnWarning()
            failBuildOnError()
        }
    }
}
job('example2') {
    publishers {
        consoleParsing {
            projectRules('unitybuilder.parser')
            unstableOnWarning()
            failBuildOnError()
        }
    }
}
