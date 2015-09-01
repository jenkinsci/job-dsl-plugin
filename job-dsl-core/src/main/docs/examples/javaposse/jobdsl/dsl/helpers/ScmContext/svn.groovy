// checkout a project into the workspace directory
job('example-1') {
    scm {
        svn('https://svn.mydomain.com/repo/project1/trunk')
    }
}

// checkout multiple projects
job('example-2') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            location('https://svn.mydomain.com/repo/project2/trunk') {
                directory('proj2')
            }
        }
    }
}

// do a sparse checkout
job('example-3') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project/trunk') {
                directory('proj2')
                depth(SvnDepth.EMPTY)
            }
        }
    }
}

// using a different checkout strategy
job('example-4') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            checkoutStrategy(SvnCheckoutStrategy.CHECKOUT)
        }
    }
}

// configure excluded and included regions
job('example-5') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            excludedRegions('/project1/trunk/.*\\.html')
            includedRegions('/project1/trunk/src/.*\\.java', '/project1/trunk/src/.*\\.groovy')
        }
    }
}

// configure excluded users, commit messages, and an excluded revision property
job('example-6') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            excludedUsers('jsmith')
            excludedUsers('jdoe', 'sally')
            excludedCommitMessages('[Bb][Aa][Dd]')
            excludedRevisionProperty('mycompany:dontbuild')
        }
    }
}

// configure repository browser
job('example-7') {
    scm {
        svn {
            location('https://svn.mydomain.com/repo/project1/trunk')
            configure { scmNode ->
                scmNode / browser(class: 'hudson.scm.browsers.FishEyeSVN') {
                    url('http://mycompany.com/fisheye/repo_name')
                    rootModule('my_root_module')
                }
            }
        }
    }
}
