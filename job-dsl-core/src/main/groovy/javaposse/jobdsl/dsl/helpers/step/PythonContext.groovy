package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class PythonContext implements Context {
    private static final List<String> VALID_NATURES = ['shell', 'xshell', 'python']

    String pythonName = 'System-CPython-2.7'
    String nature = 'shell'
    boolean ignoreExitCode = false
    String command

    /**
     * Sets the Python or shell script to execute.
     */
    void command(String command) {
        this.command = command
    }

    /**
     * If set, the build will not fail if the command exits with a non-zero exit code.
     * @param ignoreExitCode
     */
    void ignoreExitCode(boolean ignoreExitCode = true) {
        this.ignoreExitCode = ignoreExitCode
    }

    /**
     * Sets the nature of the {@link #command(java.lang.String) command} option. Must be one of {@code 'shell'}
     * (default), {@code 'xshell'} or {@code 'python'}.
     */
    void nature(String nature) {
        Preconditions.checkArgument(
            VALID_NATURES.contains(nature),
            "nature must be one of: ${VALID_NATURES.join(', ')}"
        )
        this.nature = nature
    }

    /**
     * Sets the name of the Python installation to use. Defaults to {@code 'System-CPython-2.7'}.
     */
    void pythonName(String pythonName) {
        this.pythonName = pythonName
    }
}
