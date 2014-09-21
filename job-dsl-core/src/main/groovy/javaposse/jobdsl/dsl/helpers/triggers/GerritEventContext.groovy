package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

class GerritEventContext implements Context {
    private final JobManagement jobManagement
    final List<String> eventShortNames = []

    GerritEventContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    def changeAbandoned() {
        eventShortNames << 'ChangeAbandoned'
    }

    def changeMerged() {
        eventShortNames << 'ChangeMerged'
    }

    def changeRestored() {
        eventShortNames << 'ChangeRestored'
    }

    def commentAdded() {
        eventShortNames << 'CommentAdded'
    }

    def draftPublished() {
        eventShortNames << 'DraftPublished'
    }

    def patchsetCreated() {
        eventShortNames << 'PatchsetCreated'
    }

    def refUpdated() {
        eventShortNames << 'RefUpdated'
    }

    def propertyMissing(String shortName) {
        jobManagement.logDeprecationWarning()
        eventShortNames << shortName
    }
}
