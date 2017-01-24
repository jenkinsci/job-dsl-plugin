package javaposse.jobdsl.dsl

@Deprecated
class ParametrizedConfigFile extends ConfigFile {
    List<String> arguments = []

    ParametrizedConfigFile(ConfigFileType type, JobManagement jobManagement) {
        super(type, jobManagement)
    }

    /**
     * Defines the arguments that can be passed to the script. Can be called multiple times to add more arguments.
     */
    void arguments(String... arguments) {
        this.arguments.addAll(arguments)
    }
}
