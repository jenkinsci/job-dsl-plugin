job('example-1') {
    steps {
        conditionalSteps {
            condition {
                stringsMatch('${SOME_PARAMETER}', 'pants', false)
            }
            runner('Fail')
            steps {
                shell("echo 'just one step'")
            }
        }
    }
}

job('example-2') {
    steps {
        conditionalSteps {
            condition {
                time(9, 0, 13, 0, false)
            }
            runner('Unstable')
            steps {
                shell("echo 'a first step'")
                ant('build') {
                    target('test')
                }
            }
        }
    }
}

job('example-3') {
    steps {
        conditionalSteps {
            condition {
                and {
                    status('ABORTED', 'FAILURE')
                } {
                    not {
                        fileExists('script.sh', BaseDir.WORKSPACE)
                    }
                }
            }
            runner('Unstable')
            steps {
                shell("echo 'a first step'")
                ant('build') {
                    target('test')
                }
            }
        }
    }
}
