package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class NoteToPushContext implements Context {
    String namespace
    boolean replace

    /**
     * If set, sets the namespace of the note.
     */
    void namespace(String namespace) {
        this.namespace = namespace
    }

    /**
     * If set, replaces an existing note. Defaults to {@code false}.
     */
    void replace(boolean replace = true) {
        this.replace = replace
    }
}
