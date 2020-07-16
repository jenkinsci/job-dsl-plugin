package javaposse.jobdsl.dsl

class GeneratedJob implements Comparable<GeneratedJob> {
    final String templateName
    final String jobName

    GeneratedJob(String templateName, String jobName) {
        if (jobName == null) {
            throw new IllegalArgumentException()
        }
        this.templateName = templateName
        this.jobName = jobName
    }

    @Override
    int hashCode() {
        jobName.toLowerCase().hashCode()
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
        jobName.toLowerCase() == that.jobName.toLowerCase()
    }

    @Override
    String toString() {
        "GeneratedJob{name='${jobName}'${templateName == null ? '' : ", template='${templateName}'"}}"
    }

    @Override
    int compareTo(GeneratedJob o) {
        jobName.toLowerCase() <=> o.jobName.toLowerCase()
    }
}
