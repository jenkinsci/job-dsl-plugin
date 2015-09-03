job('example') {
    steps {
        clangScanBuild {
            workspace('Mobile.xcworkspace')
            scheme('mobile.de')
            clangInstallationName('Clang Static Code Analyzer')
            targetSdk('iphonesimulator')
            configuration('Debug')
            scanBuildArgs('--use-analyzer Xcode')
            xcodeBuildArgs('-derivedDataPath $WORKSPACE/build')
        }
    }
}
