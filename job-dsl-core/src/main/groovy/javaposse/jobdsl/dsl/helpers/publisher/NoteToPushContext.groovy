package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class NoteToPushContext implements Context {
    String message
    String namespace
    boolean replace

    /**
     * Sets the content of the note.
     */
    void message(String message) {
        this.message = message
    }

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
