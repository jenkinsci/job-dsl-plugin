package javaposse.jobdsl.dsl.views.jobfilter

class JobStatusesFilter extends AbstractJobFilter {
    Set<Status> status = []

    /**
     * Selects the status of the jobs to be included or excluded. Possible values are {@code Status.UNSTABLE},
     * {@code Status.FAILED}, {@code Status.ABORTED}, {@code Status.DISABLED} or {@code Status.STABLE].
     */
    void status(Status... status) {
        this.status.addAll(status as Collection)
    }
}
