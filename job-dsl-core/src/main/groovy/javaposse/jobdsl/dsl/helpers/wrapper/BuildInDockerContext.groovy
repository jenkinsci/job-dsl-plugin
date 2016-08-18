package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class BuildInDockerContext extends AbstractContext {
    Node selector
    String dockerHostURI
    String serverCredentials
    String registryCredentials
    List<Node> volumes = []
    boolean privilegedMode
    boolean verbose
    boolean forcePull
    String userGroup
    String startCommand = '/bin/cat'

    BuildInDockerContext(JobManagement jobManagement) {
        super(jobManagement)
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

    /**
     * Always pull the image from the repository. Defaults to {@code false}.
     *
     * @since 1.43
     */
    void forcePull(boolean forcePull = true) {
        this.forcePull = forcePull
    }

    void userGroup(String userGroup) {
        this.userGroup = userGroup
    }

    void startCommand(String startCommand) {
        this.startCommand = startCommand
    }
}
