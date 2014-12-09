package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

class GerritEventContext implements Context {
    private final JobManagement jobManagement
    final List<String> eventShortNames = []

    GerritEventContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void changeAbandoned() {
        eventShortNames << 'ChangeAbandoned'
    }

    void changeMerged() {
        eventShortNames << 'ChangeMerged'
    }

    void changeRestored() {
        eventShortNames << 'ChangeRestored'
    }

    void commentAdded() {
        eventShortNames << 'CommentAdded'
    }

    void draftPublished() {
        eventShortNames << 'DraftPublished'
    }

    void patchsetCreated() {
        eventShortNames << 'PatchsetCreated'
    }

    void refUpdated() {
        eventShortNames << 'RefUpdated'
    }

    void propertyMissing(String shortName) {
        jobManagement.logDeprecationWarning()
        eventShortNames << shortName
    }
}
