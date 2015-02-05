package javaposse.jobdsl.dsl.views.jobfilter

class JobStatusesFilter extends AbstractJobFilter {
    Set<Status> status = []

    void status(Status... status) {
        this.status.addAll(status as Collection)
    }
}
