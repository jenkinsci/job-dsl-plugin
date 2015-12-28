package javaposse.jobdsl.dsl

class GeneratedUserContent {
    final String path
    final UserContent userContent

    GeneratedUserContent(UserContent userContent) {
        if (! userContent) {
            throw new IllegalArgumentException('userContent cannot be null')
        }
        this.path = userContent.path
        this.userContent = userContent
    }

    @Deprecated
    GeneratedUserContent(String path) {
        if (path == null) {
            throw new IllegalArgumentException()
        }
        this.path = path
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (o == null || getClass() != o.getClass()) {
            return false
        }

        GeneratedUserContent that = (GeneratedUserContent) o
        path == that.path
    }

    @Override
    int hashCode() {
        path.hashCode()
    }

    @Override
    String toString() {
        "GeneratedUserContent{path='${path}'}"
    }
}
