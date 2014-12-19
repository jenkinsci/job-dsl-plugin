package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement

class GerritContext implements Context {
    GerritEventContext eventContext
    Closure configureClosure
    List projects = []

    GerritContext(JobManagement jobManagement) {
        this.eventContext = new GerritEventContext(jobManagement)
    }

    Integer startedCodeReview = null
    Integer startedVerified = null

    Integer successfulCodeReview = null
    Integer successfulVerified = null

    Integer failedCodeReview = null
    Integer failedVerified = null

    Integer unstableCodeReview = null
    Integer unstableVerified = null

    Integer notBuiltCodeReview = null
    Integer notBuiltVerified = null

    void buildStarted(Integer verified, Integer codeReview) {
        startedVerified = verified
        startedCodeReview = codeReview
    }

    void buildSuccessful(Integer verified, Integer codeReview) {
        successfulVerified = verified
        successfulCodeReview = codeReview
    }

    void buildFailed(Integer verified, Integer codeReview) {
        failedVerified = verified
        failedCodeReview = codeReview
    }

    void buildUnstable(Integer verified, Integer codeReview) {
        unstableVerified = verified
        unstableCodeReview = codeReview
    }

    void buildNotBuilt(Integer verified, Integer codeReview) {
        notBuiltVerified = verified
        notBuiltCodeReview = codeReview
    }

    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    void events(Closure eventClosure) {
        ContextHelper.executeInContext(eventClosure, eventContext)
    }

    void project(String projectName, List<String> branches) {
        projects << [
                new GerritSpec(projectName),
                branches.collect { new GerritSpec(it) }
        ]
    }

    void project(String projectName, String branch) {
        project(projectName, [branch])
    }

    static class GerritSpec {
        GerritSpec(String raw) {
            int idx = raw.indexOf(':')
            String prefix = (idx == -1) ? '' : raw[0..(idx - 1)].toUpperCase()
            if (availableTypes.contains(prefix)) {
                type = prefix
                pattern = raw[(idx + 1)..-1]
            } else {
                type = 'PLAIN'
                pattern = raw
            }
        }

        Set<String> availableTypes = ['ANT', 'PLAIN', 'REG_EXP']
        String type
        String pattern
    }
}
