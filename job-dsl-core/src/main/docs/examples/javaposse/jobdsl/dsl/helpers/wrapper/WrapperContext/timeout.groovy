// defines an absolute timeout with a maximum build time of 3 minutes
job('example-1') {
    wrappers {
        timeout()
    }
}

// defines an absolute timeout with a maximum build time of one hour
job('example-2') {
    wrappers {
        timeout {
            absolute(60)
        }
    }
}

// the build will timeout when it take 3 times longer than the reference build duration of the last 3 builds
// use a 30 minutes timeout when no successful builds available as reference
job('example-3') {
    wrappers {
        timeout {
            elastic(300, 3, 30)
        }
    }
}

// abort when the build is likely to be stuck
job('example-4') {
    wrappers {
        timeout {
            likelyStuck()
        }
    }
}

// timeout if there has been no activity for 180 seconds
// then fail the build and set a build description
job('example-5') {
    wrappers {
        timeout {
            noActivity(180)
            failBuild()
            writeDescription('Build failed due to timeout after {0} minutes')
        }
    }
}

// defines an absolute timeout with a maximum build time of 5 minutes via String type
job('example-6') {
    wrappers {
        timeout {
            absolute('5')
        }
    }
}

// defines an absolute timeout using a prepare environment variable 
job('example-7') {
    wrappers {
        timeout {
          absolute('${JOB_TIMEOUT}')
        }
    }
}

// defines an absolute timeout using the string parameter
// then you can use the parameterized build to set the absolute timeout 
job('example-8') {
    parameters {
        stringParam('MY_JOB_TIMEOUT', '5', 'my description')
    }
  
    wrappers {
        timeout {
          absolute('${MY_JOB_TIMEOUT}')
        }
    }
}
