package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class PythonContext implements Context {
    private static final List<String> VALID_NATURES = ['shell', 'xshell', 'python']

    String pythonName = 'System-CPython-2.7'
    String nature = 'shell'
    boolean ignoreExitCode = false
    String command

    void command(String command) {
        this.command = command
    }

    void ignoreExitCode(boolean ignoreExitCode = true) {
        this.ignoreExitCode = ignoreExitCode
    }

    void nature(String nature) {
        Preconditions.checkArgument(
            VALID_NATURES.contains(nature),
            "nature must be one of: ${VALID_NATURES.join(', ')}"
        )
        this.nature = nature
    }

    void pythonName(String pythonName) {
        this.pythonName = pythonName
    }
}
