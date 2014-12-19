package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class TagToPushContext implements Context {
    String message
    boolean create
    boolean update

    void message(String message) {
        this.message = message
    }

    void create(boolean create = true) {
        this.create = create
    }

    void update(boolean update = true) {
        this.update = update
    }
}
