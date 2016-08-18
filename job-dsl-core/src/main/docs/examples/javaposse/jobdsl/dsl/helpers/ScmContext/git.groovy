// checkout repo1 to a sub directory and clean the workspace after checkout
job('example-1') {
    scm {
        git {
            remote {
                name('remoteB')
                url('git@server:account/repo1.git')
            }
            extensions {
                cleanAfterCheckout()
                relativeTargetDirectory('repo1')
            }
        }
    }
}

// add the upstream repository as second remote and
// merge branch featureA with master
job('example-2') {
    scm {
        git {
            remote {
                name('origin')
                url('git@serverA:account/repo1.git')
            }
            remote {
                name('upstream')
                url('git@serverB:account/repo1.git')
            }
            branch('featureA')
            extensions {
                mergeOptions {
                    remote('upstream')
                    branch('master')
                }
            }
        }
    }
}

// add user name and email options
job('example-3') {
    scm {
        git('git@git') { node -> // is hudson.plugins.git.GitSCM
            node / gitConfigName('DSL User')
            node / gitConfigEmail('me@me.com')
        }
    }
}

// add Git SCM for a GitHub repository with authentication
job('example-4') {
    scm {
        git {
            remote {
                github('account/repo', 'ssh')
                credentials('github-ci-key')
            }
        }
    }
}

// checkout at a specific branch using the alternative build choosing strategy
job('example-5') {
    scm {
        git {
            remote {
                github('account/repo', 'ssh')
            }
            branches('branch-that-may-not-exist', 'master')
            extensions {
                choosingStrategy {
                    alternative()
                }
            }
        }
    }
}
