job('example-1') {
    publishers {
        consoleParsing {
            globalRules('/var/lib/jenkins/userContent/logparser/unitybuilder.parser')
            unstableOnWarning()
            failBuildOnError()
        }
    }
}

job('example-2') {
    publishers {
        consoleParsing {
            projectRules('unitybuilder.parser')
            unstableOnWarning()
            failBuildOnError()
        }
    }
}
