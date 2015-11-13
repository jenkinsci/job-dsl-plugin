package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Context

class JobOwnershipContext implements Context {
    String primaryOwnerId
    List<String> coOwnerIds = []

    /**
     * Name of the primary owner of the job.
     */
    void primaryOwnerId(String primaryOwnerId) {
        this.primaryOwnerId = primaryOwnerId
    }

    /**
     * Add a list of coOwnerIds users.  Each call adds to the list of coowners.
     *
     * @param users a list of users to be coowners of the job.
     */
    void coOwnerIds(String... users) {
        coOwnerIds.addAll(users)
    }

    /**
     * Add a list of coOwnerIds users.  Each call adds to the list of coowners.
     *
     * @param users a list of users to be coowners of the job.
     */
    void coOwnerIds(Iterable<String> users) {
        coOwnerIds += users
    }
}
