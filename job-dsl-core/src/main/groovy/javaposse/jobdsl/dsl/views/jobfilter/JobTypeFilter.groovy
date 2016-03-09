package javaposse.jobdsl.dsl.views.jobfilter

class JobTypeFilter extends AbstractJobFilter {
    JobType type = JobType.FREE_STYLE_PROJECT

    /**
     * Selects the job type to be matched. Defaults to {@code JobType.FREE_STYLE_PROJECT}.
     */
    void type(JobType type) {
        this.type = type
    }
}
