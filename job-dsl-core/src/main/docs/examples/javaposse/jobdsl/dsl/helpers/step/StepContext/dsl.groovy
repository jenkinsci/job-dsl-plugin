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

// run the scripts in the sandbox, be strict about missing plugins and collisions with other seed jobs, and
// delete everything that is no longer generated
job('example-4') {
    steps {
        dsl {
            external('jobs/*.groovy')
            sandbox()
            failOnMissingPlugin()
            failOnSeedCollision()
            unstableOnDeprecation()
            removeAction('DELETE')
            removeViewAction('DELETE')
            removeConfigFilesAction('DELETE')
        }
    }
}
