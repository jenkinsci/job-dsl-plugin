job('example') {
    steps {
        conditionalSteps {
            condition {
                stringsMatch('${SOME_PARAMETER}', 'pants', false)
            }
        }
    }
    configure { project ->
        project / builders / 'org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder' / conditionalbuilders << 'hudson.tasks.Shell' {
            command('echo Hello')
        }
    }
}
