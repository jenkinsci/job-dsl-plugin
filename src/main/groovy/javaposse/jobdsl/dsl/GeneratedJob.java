package javaposse.jobdsl.dsl;

public class GeneratedJob {
    private String templateName;
    private String jobName;
    private boolean created;

    public GeneratedJob(String templateName, String jobName, boolean created) {
        super();
        this.templateName = templateName;
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public boolean isCreated() {
        return created;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
        result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeneratedJob other = (GeneratedJob) obj;
        if (jobName == null) {
            if (other.jobName != null)
                return false;
        } else if (!jobName.equals(other.jobName))
            return false;
        if (templateName == null) {
            if (other.templateName != null)
                return false;
        } else if (!templateName.equals(other.templateName))
            return false;
        return true;
    }

}
