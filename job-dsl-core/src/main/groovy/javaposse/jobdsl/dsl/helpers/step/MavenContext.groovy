package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation

class MavenContext implements javaposse.jobdsl.dsl.helpers.common.MavenContext {
    String rootPOM
    List<String> goals = []
    List<String> mavenOpts = []
    LocalRepositoryLocation localRepositoryLocation
    String mavenInstallation = '(Default)'
    Closure configureBlock
    
    @Override
    def rootPOM(String rootPOM) {
        this.rootPOM = rootPOM
    }

    @Override
    def goals(String goals) {
        this.goals << goals
    }

    @Override
    def mavenOpts(String mavenOpts) {
        this.mavenOpts << mavenOpts
    }

    @Override
    def localRepository(LocalRepositoryLocation location) {
        this.localRepositoryLocation = location
    }

    @Override
    def mavenInstallation(String name) {
        this.mavenInstallation = name
    }
    
    def configure(Closure closure) {
        this.configureBlock = closure
    }
}
