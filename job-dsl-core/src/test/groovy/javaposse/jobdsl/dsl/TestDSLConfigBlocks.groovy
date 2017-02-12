package javaposse.jobdsl.dsl

class TestDSLConfigBlocks {
    // triggers step
    static Closure polling() {
        return {
            scmTrigger {
                scmpoll_spec('H * * *')
                // Ignore changes notified by SCM post-commit hooks.
                ignorePostCommitHooks(false)
            }
        }
    }

    // raw config block
    static Closure rawConfigProperties() {
        return {
            it / 'properties' / 'com.example.Test' {
                'switch'('on')
            }
        }
    }
}
