package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class TagToPushContext implements Context {
    String message
    boolean create
    boolean update

    /**
     * Sets a message for the tag.
     */
    void message(String message) {
        this.message = message
    }

    /**
     * If set, creates a new tag. Defaults to {@code false}.
     */
    void create(boolean create = true) {
        this.create = create
    }

    /**
     * If set, updates an existing tag. Defaults to {@code false}.
     */
    void update(boolean update = true) {
        this.update = update
    }
}
