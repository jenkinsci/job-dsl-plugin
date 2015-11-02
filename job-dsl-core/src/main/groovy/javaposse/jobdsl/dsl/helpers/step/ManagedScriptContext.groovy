package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class ManagedScriptContext implements Context {
    List<String> arguments = []
    boolean tokenized

    /**
     * Specifies the arguments to pass to the script. Can be called multiple times to add more arguments.
     */
    void arguments(String... arguments) {
        this.arguments.addAll(arguments)
    }

    /**
     * If set, decomposes the given value of a each argument into multiple arguments by splitting via whitespace.
     * Defaults to {@code false}.
     */
    void tokenized(boolean tokenized = true) {
        this.tokenized = tokenized
    }
}
