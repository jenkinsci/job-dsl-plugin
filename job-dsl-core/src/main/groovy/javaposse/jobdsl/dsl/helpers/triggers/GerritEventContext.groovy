package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.Context

class GerritEventContext implements Context {
    final List<String> eventShortNames = []

    /**
     * Trigger when a change is abandoned.
     *
     * @since 1.26
     */
    void changeAbandoned() {
        eventShortNames << 'ChangeAbandoned'
    }

    /**
     * Trigger when a change is merged/submitted.
     *
     * @since 1.26
     */
    void changeMerged() {
        eventShortNames << 'ChangeMerged'
    }

    /**
     * Trigger when a change is restored.
     *
     * @since 1.26
     */
    void changeRestored() {
        eventShortNames << 'ChangeRestored'
    }

    /**
     * Trigger when a review comment is left with the indicated vote category and value.
     *
     * @since 1.26
     */
    void commentAdded() {
        eventShortNames << 'CommentAdded'
    }

    /**
     * Trigger when a draft change or patch set is published.
     *
     * @since 1.26
     */
    void draftPublished() {
        eventShortNames << 'DraftPublished'
    }

    /**
     * Trigger when a new change or patch set is uploaded.
     *
     * @since 1.26
     */
    void patchsetCreated() {
        eventShortNames << 'PatchsetCreated'
    }

    /**
     * Trigger when a reference (e.g., branch or tag) is updated.
     *
     * @since 1.26
     */
    void refUpdated() {
        eventShortNames << 'RefUpdated'
    }
}
