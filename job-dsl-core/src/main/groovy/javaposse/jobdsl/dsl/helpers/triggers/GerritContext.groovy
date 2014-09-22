package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class GerritContext implements Context {
    GerritEventContext eventContext = new GerritEventContext()
    Closure configureClosure
    def projects = []

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

    def buildStarted(Integer verified, Integer codeReview) {
        startedVerified = verified
        startedCodeReview = codeReview
    }

    def buildSuccessful(Integer verified, Integer codeReview) {
        successfulVerified = verified
        successfulCodeReview = codeReview
    }

    def buildFailed(Integer verified, Integer codeReview) {
        failedVerified = verified
        failedCodeReview = codeReview
    }

    def buildUnstable(Integer verified, Integer codeReview) {
        unstableVerified = verified
        unstableCodeReview = codeReview
    }

    def buildNotBuilt(Integer verified, Integer codeReview) {
        notBuiltVerified = verified
        notBuiltCodeReview = codeReview
    }

    def configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    def events(Closure eventClosure) {
        AbstractContextHelper.executeInContext(eventClosure, eventContext)
    }

    def project(String projectName, List<String> branches) {
        project(projectName, branches, [])
    }

    def project(String projectName, String branch) {
        project(projectName, [branch])
    }

    def project(String projectName, List<String> branches, List<String> topics) {
         projects << [
                new GerritSpec(projectName),
                branches.collect { new GerritSpec(it) },
                topics.collect { new GerritSpec(it) }
        ]
    }

    def project(String projectName, String branch, List<String> topics) {
        project(projectName, [branch], topics)
    }

    def project(String projectName, String branch, String topic) {
        project(projectName, [branch], [topic])
    }

    static class GerritSpec {
        GerritSpec(String raw) {
            def idx = raw.indexOf(':')
            def prefix = (idx == -1) ? '' : raw[0..(idx - 1)].toUpperCase()
            if (availableTypes.contains(prefix)) {
                type = prefix
                pattern = raw[(idx + 1)..-1]
            } else {
                type = 'PLAIN'
                pattern = raw
            }
        }

        def availableTypes = ['ANT', 'PLAIN', 'REG_EXP']
        String type
        String pattern
    }
}
