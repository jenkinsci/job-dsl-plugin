job('example') {
    steps {
        updateBuildName {
            buildNameFilePath('version.txt')
            buildNameMacroTemplate('#${BUILD_NUMBER}')
            readFromFile(true)
            useMacro(true)
            insertMacroFirst(true)
        }
    }
}
