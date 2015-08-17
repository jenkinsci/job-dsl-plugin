package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class HgContext extends AbstractContext {
    String installation
    List<String> modules = []
    String subdirectory
    String branch
    String tag
    String credentialsId
    boolean clean
    boolean disableChangeLog
    Closure withXmlClosure

    HgContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void installation(String installation) {
        this.installation = installation
    }

    void modules(String... modules) {
        this.modules.addAll(modules)
    }

    void subdirectory(String subdirectory) {
        this.subdirectory = subdirectory
    }

    void branch(String branch) {
        this.branch = branch
    }

    void tag(String tag) {
        this.tag = tag
    }

    void credentials(String credentials) {
        this.credentialsId = credentials
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
