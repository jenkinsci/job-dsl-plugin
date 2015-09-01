package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class RbenvContext implements Context {
    boolean ignoreLocalVersion = false
    List<String> gems = []
    String root = '$HOME/.rbenv'
    String rbenvRepository = 'https://github.com/sstephenson/rbenv.git'
    String rbenvRevision = 'master'
    String rubyBuildRepository = 'https://github.com/sstephenson/ruby-build.git'
    String rubyBuildRevision = 'master'

    /**
     * Defaults to {@code false}.
     */
    void ignoreLocalVersion(boolean ignore = true) {
        this.ignoreLocalVersion = ignore
    }

    /**
     * Specifies which gems should be pre-installed.
     */
    void gems(String... gems) {
        this.gems.addAll(gems)
    }

    /**
     * Sets the {@code RBENV_ROOT}. Defaults to {@code '$HOME/.rbenv'}.
     */
    void root(String root) {
        this.root = root
    }

    /**
     * Specifies a rbenv git repository. Defaults to {@code 'https://github.com/sstephenson/rbenv.git'}.
     */
    void rbenvRepository(String repository) {
        this.rbenvRepository = repository
    }

    /**
     * Specifies a branch in the rbenv git repository. Defaults to {@code 'master'}.
     */
    void rbenvRevision(String revision) {
        this.rbenvRevision = revision
    }

    /**
     * Specifies a ruby-build git repository. Defaults to {@code 'https://github.com/sstephenson/ruby-build.git'}.
     */
    void rubyBuildRepository(String repository) {
        this.rubyBuildRepository = repository
    }

    /**
     * Specifies a branch in the ruby-build git repository. Defaults to {@code 'master'}.
     */
    void rubyBuildRevision(String revision) {
        this.rubyBuildRevision = revision
    }
}
