package javaposse.jobdsl.dsl.helpers.triggers

import javaposse.jobdsl.dsl.helpers.Context


class GerritEventContext extends Expando implements Context {

    def eventShortNames = []

    enum GerritEvents {
        ChangeAbandoned,
        ChangeMerged,
        ChangeRestored,
        DraftPublished,
        PatchsetCreated,
        RefUpdated
        // not supported event:
        //CommentAdded// because of verdictCategory/etc <verdictCategory>CRVW</verdictCategory><commentAddedTriggerApprovalValue/>
    };

    @Override
    public Object getProperty(String property) {
        if (property == 'eventShortNames') {
            return eventShortNames
        }
        try {
            GerritEvents.valueOf(property)
            eventShortNames << property
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("invalid GerritEvent ${property}, valid: ${GerritEvents.values()}", iae)
        }
    }
}
