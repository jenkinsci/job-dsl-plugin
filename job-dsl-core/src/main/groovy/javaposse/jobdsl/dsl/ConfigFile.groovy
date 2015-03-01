package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions

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

    @Deprecated
    void name(String name) {
        jobManagement.logDeprecationWarning()
        this.name = name
    }

    void comment(String comment) {
        Preconditions.checkArgument(comment != null, 'comment must not be null')

        this.comment = comment
    }

    void content(String content) {
        Preconditions.checkArgument(content != null, 'content must not be null')

        this.content = content
    }
}
