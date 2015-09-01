package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class CredentialsParameterContext implements Context {
    String type = 'com.cloudbees.plugins.credentials.common.StandardCredentials'
    boolean required
    String defaultValue
    String description

    /**
     * Specifies the type of credentials which should be selectable by the parameter.
     *
     * Must be a fully-qualified Java class name of a credentials type. Possible values include
     * {@code 'com.cloudbees.plugins.credentials.common.StandardCredentials'} (default, allows any type to be chosen),
     * {@code 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl'}, or
     * {@code 'com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl'}. Additional credentials types are
     * provided by other plugins, e.g.
     * {@code 'com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey'} is provided by the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/SSH+Credentials+Plugin">SSH Credentials Plugin</a> and
     * {@code 'org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl'} and
     * {@code 'org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl'} are provided by the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Plain+Credentials+Plugin">Plain Credentials Plugin</a>.
     */
    void type(String type) {
        Preconditions.checkNotNullOrEmpty(type, 'type must not be null or empty')
        this.type = type
    }

    /**
     * If set, a value must be selected.
     */
    void required(boolean required = true) {
        this.required = required
    }

    /**
     * Sets the default value for the parameter.
     */
    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    /**
     * Sets a description for the parameter.
     */
    void description(String description) {
        this.description = description
    }
}
