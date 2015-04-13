package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class GerritEventContext implements Context {
    final List<String> eventShortNames = []

    /**
     * @since 1.26
     */
    void changeAbandoned() {
        eventShortNames << 'ChangeAbandoned'
    }

    /**
     * @since 1.26
     */
    void changeMerged() {
        eventShortNames << 'ChangeMerged'
    }

    /**
     * @since 1.26
     */
    void changeRestored() {
        eventShortNames << 'ChangeRestored'
    }

    /**
     * @since 1.26
     */
    void commentAdded() {
        eventShortNames << 'CommentAdded'
    }

    /**
     * @since 1.26
     */
    void draftPublished() {
        eventShortNames << 'DraftPublished'
    }

    /**
     * @since 1.26
     */
    void patchsetCreated() {
        eventShortNames << 'PatchsetCreated'
    }

    /**
     * @since 1.26
     */
    void refUpdated() {
        eventShortNames << 'RefUpdated'
    }
}
