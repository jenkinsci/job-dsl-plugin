package javaposse.jobdsl.dsl.helpers.scm

import groovy.lang.Closure;
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class HgContext implements Context {
    private final List<WithXmlAction> withXmlActions
    private final JobManagement jobManagement

    String installation
    String url
    List<String> modules = []
    String subDirectory
    String branch
    String tag
    String credentialsId
    boolean clean = false
    boolean disableChangeLog = false
    Closure withXmlClosure

    HgContext(List<WithXmlAction> withXmlActions, JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.withXmlActions = withXmlActions
    }

    void installation(String installation) {
        this.installation = installation
    }

    void url(String url) {
        this.url = url
    }

    void modul(String modul) {
        this.modules.add(modul)
    }

    void modules(String... modules) {
        this.modules.addAll(modules)
    }

    void subDirectory(String subDirectory) {
        this.subDirectory = subDirectory
    }

    void branch(String branch) {
        this.branch = branch
    }

    void tag(String tag) {
        this.tag = tag
    }

    void credentials(String credentials) {
        this.credentialsId = jobManagement.getCredentialsId(credentials)
    }

    void clean(boolean clean = true) {
        this.clean = clean
    }
    
    void disableChangeLog(boolean disableChangeLog = true) {
        this.disableChangeLog = disableChangeLog
    }

    void configure(Closure withXmlClosure) {
        this.withXmlClosure = withXmlClosure
    }
}
