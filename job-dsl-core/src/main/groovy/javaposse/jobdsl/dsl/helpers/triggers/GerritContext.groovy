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

    def buildStarted(Object verified, Object codeReview) {
        buildStarted(
                Integer.parseInt(verified.toString()),
                Integer.parseInt(codeReview.toString())
        )
    }


    def buildSuccessful(Integer verified, Integer codeReview) {
        successfulVerified = verified
        successfulCodeReview = codeReview
    }

    def buildSuccessful(Object verified, Object codeReview) {
        buildSuccessful(
                Integer.parseInt(verified.toString()),
                Integer.parseInt(codeReview.toString())
        )
    }

    def buildFailed(Integer verified, Integer codeReview) {
        failedVerified = verified
        failedCodeReview = codeReview
    }

    def buildFailed(Object verified, Object codeReview) {
        buildFailed(
                Integer.parseInt(verified.toString()),
                Integer.parseInt(codeReview.toString())
        )
    }

    def buildUnstable(Integer verified, Integer codeReview) {
        unstableVerified = verified
        unstableCodeReview = codeReview
    }

    def buildUnstable(Object verified, Object codeReview) {
        buildUnstable(
                Integer.parseInt(verified.toString()),
                Integer.parseInt(codeReview.toString())
        )
    }

    def buildNotBuilt(Integer verified, Integer codeReview) {
        notBuiltVerified = verified
        notBuiltCodeReview = codeReview
    }

    def buildNotBuilt(Object verified, Object codeReview) {
        buildNotBuilt(
                Integer.parseInt(verified.toString()),
                Integer.parseInt(codeReview.toString())
        )
    }

    def configure(Closure configureClosure) {
        // save for later
        this.configureClosure = configureClosure
    }

    def events(Closure eventClosure) {
        AbstractContextHelper.executeInContext(eventClosure, eventContext)
    }

    def project(String projectName, List<String> branches) {
        projects << [new GerritSpec(projectName), branches.collect { new GerritSpec(it) }]
    }

    def project(String projectName, String branch) {
        project(projectName, [branch])
    }

    static class GerritSpec {
        GerritSpec(String raw) {
            def idx = raw.indexOf(':')
            def prefix = (idx == -1) ? '' : raw.substring(0, idx).toUpperCase()
            if (availableTypes.contains(prefix)) {
                type = prefix
                pattern = raw.substring(idx + 1)
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
