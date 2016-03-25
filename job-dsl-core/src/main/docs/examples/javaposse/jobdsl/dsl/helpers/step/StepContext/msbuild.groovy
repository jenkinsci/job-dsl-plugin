job('example') {
    steps {
        msbuild {
            msbuildInstallation('MSBuild 1.8')
            buildFile('dir1/build.proj')
            args('check')
            args('another')
            passBuildVariables()
            continueOnBuildFailure()
            unstableIfWarnings()
        }
    }
}
