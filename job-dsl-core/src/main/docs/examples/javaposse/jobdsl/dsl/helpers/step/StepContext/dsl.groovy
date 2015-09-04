job('example-1') {
    steps {
        dsl {
            external('projectA.groovy', 'projectB.groovy')
            external('projectC.groovy')
            removeAction('DISABLE')
            ignoreExisting()
            additionalClasspath('lib')
        }
    }
}

job('example-2') {
    steps {
        dsl(['projectA.groovy', 'projectB.groovy'], 'DELETE')
    }
}

job('example-3') {
    steps {
        dsl {
            text(readFileFromWorkspace('more-jobs.groovy'))
            removeAction('DELETE')
        }
    }
}
