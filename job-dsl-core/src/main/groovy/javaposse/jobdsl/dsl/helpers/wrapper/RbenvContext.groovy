package javaposse.jobdsl.dsl.helpers.wrapper

import  javaposse.jobdsl.dsl.helpers.Context

class RbenvContext implements Context {
    boolean ignoreLocalVersion = false
    List<String> gems = []
    String root = '$HOME/.rbenv'
    String rbenvRepository = 'https://github.com/sstephenson/rbenv.git'
    String rbenvRevision = 'master'
    String rubyBuildRepository = 'https://github.com/sstephenson/ruby-build.git'
    String rubyBuildRevision = 'master'

    void ignoreLocalVersion(boolean ignore = true) {
        this.ignoreLocalVersion = ignore
    }

    void gems(String... gems) {
        this.gems.addAll(gems)
    }

    void root(String root) {
        this.root = root
    }

    void rbenvRepository(String repository) {
        this.rbenvRepository = repository
    }

    void rbenvRevision(String revision) {
        this.rbenvRevision = revision
    }

    void rubyBuildRepository(String repository) {
        this.rubyBuildRepository = repository
    }

    void rubyBuildRevision(String revision) {
        this.rubyBuildRevision = revision
    }
}
