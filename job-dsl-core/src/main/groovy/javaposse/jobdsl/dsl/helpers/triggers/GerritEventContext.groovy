package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class GerritEventContext implements Context {
    final List<String> eventShortNames = []

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
}
