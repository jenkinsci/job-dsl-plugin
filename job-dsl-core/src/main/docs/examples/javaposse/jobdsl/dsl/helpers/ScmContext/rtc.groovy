job('example-1') {
    scm {
        rtc {
            buildDefinition('buildDefinitionInRTC')
        }
    }
}

job('example-2') {
    scm {
        rtc {
            buildWorkspace('some-workspace')
            connection('my-build-tool', 'build-user', 'https://localhost:9444/ccm', 60)
        }
    }
}
