package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class BuildInDockerContext implements Context {
    Node selector
    String dockerHostURI
    String serverCredentials
    String registryCredentials
    List<Node> volumes = []
    boolean privilegedMode
    boolean verbose
    String userGroup
    String startCommand = '/bin/cat'

    BuildInDockerContext() {
        dockerfile()
    }

    void image(String image) {
        selector = new NodeBuilder().
                'selector'(class: 'com.cloudbees.jenkins.plugins.okidocki.PullDockerImageSelector') {
            delegate.image(image ?: '')
        }
    }

    void dockerfile(String contextPath = '.', String dockerfile = 'Dockerfile') {
        selector = new NodeBuilder().
                'selector'(class: 'com.cloudbees.jenkins.plugins.okidocki.DockerfileImageSelector') {
            delegate.contextPath(contextPath ?: '')
            delegate.dockerfile(dockerfile ?: '')
        }
    }

    void dockerHostURI(String dockerHostURI) {
        this.dockerHostURI = dockerHostURI
    }

    void serverCredentials(String serverCredentials) {
        this.serverCredentials = serverCredentials
    }

    void registryCredentials(String registryCredentials) {
        this.registryCredentials = registryCredentials
    }

    void volume(String pathOnHost, String pathInsideContainer) {
        volumes << new NodeBuilder().'com.cloudbees.jenkins.plugins.okidocki.Volume' {
            hostPath(pathOnHost ?: '')
            path(pathInsideContainer ?: '')
        }
    }

    void privilegedMode(boolean privilegedMode = true) {
        this.privilegedMode = privilegedMode
    }

    void verbose(boolean verbose = true) {
        this.verbose = verbose
    }

    void userGroup(String userGroup) {
        this.userGroup = userGroup
    }

    void startCommand(String startCommand) {
        this.startCommand = startCommand
    }
}
