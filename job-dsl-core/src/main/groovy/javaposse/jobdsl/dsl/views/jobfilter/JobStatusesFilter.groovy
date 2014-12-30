package javaposse.jobdsl.dsl.views.jobfilter

class JobStatusesFilter extends AbstractJobFilter {
    Set<Status> status = [] as Set

    final String className = 'hudson.views.JobStatusFilter'

    protected void addArgs(NodeBuilder builder) {
        super.addArgs(builder)
        Status.values().each { status ->
            builder."${status.name().toLowerCase()}"(this.status.contains(status))
        }
    }

    void status(Status... status) {
        this.status = status as Set
    }
}
