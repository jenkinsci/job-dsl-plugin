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

    /**
     * Adds a single task to execute.
     */
    void task(String task) {
        this.tasks << task
    }

    /**
     * Add a list of tasks to execute.
     */
    void tasks(Iterable<String> tasks) {
        tasks.each {
            task(it)
        }
    }

    /**
     * Sets the path to a Rakefile. Defaults to {@code 'Rakefile'}.
     */
    void file(String file) {
        this.file = file
    }

    /**
     * Specifies the Ruby installation to use.
     */
    void installation(String installation) {
        this.installation = installation
    }

    /**
     * Sets the path to Rake library directory. Defaults to {@code 'rakelib'}.
     */
    void libDir(String libDir) {
        this.libDir = libDir
    }

    /**
     * Specifies the path the working directory in which Rake should be executed. Defaults to the workspace directory.
     */
    void workingDir(String workingDir) {
        this.workingDir = workingDir
    }

    /**
     * Executes Rake with Bundler 'bundle exec rake'. Defaults to {@code false}.
     */
    void bundleExec(boolean bundleExec = true) {
        this.bundleExec = bundleExec
    }

    /**
     * Prevents logging of messages or announcements to standard output. Defaults to {@code false}.
     */
    void silent(boolean silent = true) {
        this.silent = silent
    }
}
