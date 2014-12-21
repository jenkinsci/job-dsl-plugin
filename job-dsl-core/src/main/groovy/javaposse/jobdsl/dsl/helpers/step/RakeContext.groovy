package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class RakeContext implements Context {
    List<String> tasks = []
    String file = ''
    String installation = '(Default)'
    String libDir = ''
    String workingDir = ''
    boolean bundleExec = false
    boolean silent = false

    void task(String task) {
        this.tasks << task
    }

    void tasks(Iterable<String> tasks) {
        tasks.each {
            task(it)
        }
    }

    void file(String file) {
        this.file = file
    }

    void installation(String installation) {
        this.installation = installation
    }

    void libDir(String libDir) {
        this.libDir = libDir
    }

    void workingDir(String workingDir) {
        this.workingDir = workingDir
    }

    void bundleExec(boolean bundleExec = true) {
        this.bundleExec = bundleExec
    }

    void silent(boolean silent = true) {
        this.silent = silent
    }
}
