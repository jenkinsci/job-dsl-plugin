mavenJob('example') {
    wrappers {
        mavenRelease {
            scmUserEnvVar('MY_USER_ENV')
            scmPasswordEnvVar('MY_PASSWORD_ENV')
            releaseEnvVar('RELEASE_ENV')
            releaseGoals('release:prepare release:perform')
            dryRunGoals('-DdryRun=true release:prepare')
            selectCustomScmCommentPrefix()
            selectAppendJenkinsUsername()
            selectScmCredentials()
            numberOfReleaseBuildsToKeep(10)
        }
    }
}
