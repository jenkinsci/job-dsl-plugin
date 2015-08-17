package javaposse.jobdsl.dsl

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

    void comment(String comment) {
        Preconditions.checkNotNull(comment, 'comment must not be null')

        this.comment = comment
    }

    void content(String content) {
        Preconditions.checkNotNull(content, 'content must not be null')

        this.content = content
    }
}
