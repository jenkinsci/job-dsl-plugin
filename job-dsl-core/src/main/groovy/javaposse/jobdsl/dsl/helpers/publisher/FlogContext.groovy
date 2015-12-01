package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class FlogContext implements Context {
    String rubyDirectories

    /**
     * Specifies the ruby directories to monitorize with Flog, relative to the workspace.
     */
    void rubyDirectories(String rubyDirectories) {
        this.rubyDirectories = rubyDirectories
    }
}
