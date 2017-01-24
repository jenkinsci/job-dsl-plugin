package javaposse.jobdsl.dsl

@Deprecated
class ConfigFile implements Context {
    final ConfigFileType type
    final JobManagement jobManagement
    String name
    String comment = ''
    String content = ''

    ConfigFile(ConfigFileType type, JobManagement jobManagement) {
        this.type = type
        this.jobManagement = jobManagement
    }

    /**
     * Sets a comment for the config file.
     */
    void comment(String comment) {
        Preconditions.checkNotNull(comment, 'comment must not be null')

        this.comment = comment
    }

    /**
     * Sets the content for the config file. Use {@link DslFactory#readFileFromWorkspace(java.lang.String)} to read the
     * content from a file.
     */
    void content(String content) {
        Preconditions.checkNotNull(content, 'content must not be null')

        this.content = content
    }
}
