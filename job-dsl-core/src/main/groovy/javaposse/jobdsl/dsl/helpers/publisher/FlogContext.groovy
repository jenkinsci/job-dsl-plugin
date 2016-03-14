package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class FlogContext implements Context {
    List<String> rubyDirectories = []

    /**
     * Specifies the Ruby directories to monitorize with Flog, relative to the workspace. Can be called multiple times
     * to add more directories.
     */
    void rubyDirectories(String... rubyDirectories) {
        this.rubyDirectories.addAll(rubyDirectories)
    }
}
