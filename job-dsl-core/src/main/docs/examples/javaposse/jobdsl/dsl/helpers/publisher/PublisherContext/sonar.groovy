// run Sonar analysis for feature-xy branch,
// but skip if SKIP_SONAR environment variable is set to true
job('example') {
    publishers {
        sonar {
            branch('feature-xy')
            overrideTriggers {
                skipIfEnvironmentVariable('SKIP_SONAR')
            }
        }
    }
}
