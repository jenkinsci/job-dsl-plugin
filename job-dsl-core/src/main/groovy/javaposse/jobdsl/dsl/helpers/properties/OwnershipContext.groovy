package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Context

class OwnershipContext implements Context {
    String primaryOwnerId
    List<String> coOwnerIds = []

    /**
     * Sets the name of the primary owner of the job.
     */
    void primaryOwnerId(String primaryOwnerId) {
        this.primaryOwnerId = primaryOwnerId
    }

    /**
     * Adds additional users, who have ownership privileges. Can be called multiple times to add more users.
     */
    void coOwnerIds(String... userIds) {
        coOwnerIds.addAll(userIds)
    }

    /**
     * Adds additional users, who have ownership privileges. Can be called multiple times to add more users.
     */
    void coOwnerIds(Iterable<String> userIds) {
        coOwnerIds += userIds
    }
}
