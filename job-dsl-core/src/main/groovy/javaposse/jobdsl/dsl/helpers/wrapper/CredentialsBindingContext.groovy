package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

class CredentialsBindingContext implements Context {
    private final JobManagement jobManagement

    Map<String, String> file = [:]
    Map<String, String> string = [:]
    Map<String, String> usernamePassword = [:]
    Map<String, String> zipFile = [:]

    CredentialsBindingContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void file(String variable, String credentials) {
        this.file[variable] = jobManagement.getCredentialsId(credentials)
    }

    void string(String variable, String credentials) {
        this.string[variable] = jobManagement.getCredentialsId(credentials)
    }

    void usernamePassword(String variable, String credentials) {
        this.usernamePassword[variable] = jobManagement.getCredentialsId(credentials)
    }

    void zipFile(String variable, String credentials) {
        this.zipFile[variable] = jobManagement.getCredentialsId(credentials)
    }
}
