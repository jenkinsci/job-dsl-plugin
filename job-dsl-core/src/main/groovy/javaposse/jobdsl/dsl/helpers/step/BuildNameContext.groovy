package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class BuildNameContext implements Context {
    String buildNameFilePath = 'version.txt'
    String buildNameMacroTemplate = '#${BUILD_NUMBER}'
    boolean readFromFile
    boolean useMacro
    boolean insertMacroFirst

    /**
     * Sets the path to the file to read the build name from.
     */
    void buildNameFilePath(String buildNameFilePath) {
        this.buildNameFilePath = buildNameFilePath
    }

    /**
     * Sets a macro template to evaluate.
     */
    void buildNameMacroTemplate(String buildNameMacroTemplate) {
        this.buildNameMacroTemplate = buildNameMacroTemplate
    }

    /**
     * Specifies whether to read the build name from a file.
     */
    void readFromFile(boolean readFromFile = true) {
        this.readFromFile = readFromFile
    }

    /**
     * Specifies whether to use a macro.
     */
    void useMacro(boolean useMacro = true) {
        this.useMacro = useMacro
    }

    /**
     * If true, the result of the macro is inserted first.
     */
    void insertMacroFirst(boolean insertMacroFirst = true) {
        this.insertMacroFirst = insertMacroFirst
    }
}
