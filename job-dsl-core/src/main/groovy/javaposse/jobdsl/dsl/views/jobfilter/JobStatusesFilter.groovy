package javaposse.jobdsl.dsl.views.jobfilter

class JobStatusesFilter extends AbstractJobFilter {
    Set<Status> status = []

    /**
     * Selects the status of the jobs to be included or excluded.
     */
    void status(Status... status) {
        this.status.addAll(status as Collection)
    }
}
