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

    def ignoreLocalVersion(boolean ignore = true) {
        this.ignoreLocalVersion = ignore
    }

    def gems(String... gems) {
        this.gems.addAll(gems)
    }

    def root(String root) {
        this.root = root
    }

    def rbenvRepository(String repository) {
        this.rbenvRepository = repository
    }

    def rbenvRevision(String revision) {
        this.rbenvRevision = revision
    }

    def rubyBuildRepository(String repository) {
        this.rubyBuildRepository = repository
    }

    def rubyBuildRevision(String revision) {
        this.rubyBuildRevision = revision
    }
}
