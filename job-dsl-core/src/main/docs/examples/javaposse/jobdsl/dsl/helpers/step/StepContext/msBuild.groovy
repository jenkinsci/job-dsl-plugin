job('example') {
    steps {
        msBuild {
            msBuildInstallation('MSBuild 1.8')
            buildFile('dir1/build.proj')
            args('check')
            args('another')
            passBuildVariables()
            continueOnBuildFailure()
            unstableIfWarnings()
        }
    }
}
