package javaposse.jobdsl.dsl

class GeneratedUserContent implements Comparable<GeneratedUserContent> {
    final String path

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

    @Override
    int compareTo(GeneratedUserContent o) {
        path <=> o.path
    }
}
