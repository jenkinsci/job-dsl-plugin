package javaposse.jobdsl.dsl;

public class GeneratedJob {
    private String templateName;
    private String jobName;

    public GeneratedJob(String templateName, String jobName) {
        if (jobName == null) {
            throw new IllegalArgumentException();
        }
        this.templateName = templateName;
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getTemplateName() {
        return templateName;
    }

    @Override
    public int hashCode() {
        return jobName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneratedJob that = (GeneratedJob) o;
        return jobName.equals(that.jobName);
    }

    @Override
    public String toString() {
        return "GeneratedJob{" +
                "name='" + jobName + "'" +
                (templateName == null ? "" : (", template='" + templateName + "'")) +
                "}";
    }
}
