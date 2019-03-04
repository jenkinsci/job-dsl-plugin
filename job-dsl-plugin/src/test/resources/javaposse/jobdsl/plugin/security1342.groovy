package javaposse.jobdsl.plugin

import hudson.model.FreeStyleProject
import jenkins.model.Jenkins

class Foo extends JenkinsJobParent {
    Foo() { Jenkins.get().createProject(FreeStyleProject, 'should-not-exist') }

    @Override
    Object run() {
        return null
    }
}
