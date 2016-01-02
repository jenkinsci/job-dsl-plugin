package javaposse.jobdsl.dsl

class GeneratedJob {
    final String templateName
    final String jobName
    final Item item

    GeneratedJob(String templateName, Item item) {
        if (! item) {
            throw new IllegalArgumentException('item cannot be null')
        }
        this.templateName = templateName
        this.jobName = item.name
        this.item = item
    }

    @Deprecated
    GeneratedJob(String templateName, String jobName) {
        if (jobName == null) {
            throw new IllegalArgumentException()
        }
        this.templateName = templateName
        this.jobName = jobName
    }

    @Override
    int hashCode() {
        jobName.hashCode()
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (o == null || getClass() != o.getClass()) {
            return false
        }

        GeneratedJob that = (GeneratedJob) o
        jobName == that.jobName
    }

    @Override
    String toString() {
        "GeneratedJob{name='${jobName}'${templateName == null ? '' : ", template='${templateName}'"}}"
    }
}
