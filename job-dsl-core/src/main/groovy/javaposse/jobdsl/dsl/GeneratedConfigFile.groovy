package javaposse.jobdsl.dsl

class GeneratedConfigFile implements Comparable<GeneratedConfigFile> {
    final String id
    final String name
    final ConfigFile configFile

    GeneratedConfigFile(String id, ConfigFile configFile) {
        if (! id) {
            throw new IllegalArgumentException('id cannot be null')
        }
        if (! configFile) {
            throw new IllegalArgumentException('configFile cannot be null')
        }
        this.id = id
        this.name = configFile.name
        this.configFile = configFile
    }

    @Deprecated
    GeneratedConfigFile(String id, String name) {
        if (id == null || name == null) {
            throw new IllegalArgumentException()
        }
        this.id = id
        this.name = name
    }

    @Override
    int hashCode() {
        id.hashCode()
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (!(o instanceof GeneratedConfigFile)) {
            return false
        }

        GeneratedConfigFile that = (GeneratedConfigFile) o
        id == that.id
    }

    @Override
    String toString() {
        "GeneratedConfigFile{name='${name}', id='${id}'}"
    }

    @Override
    int compareTo(GeneratedConfigFile o) {
        name <=> o.name
    }
}
