package javaposse.jobdsl.dsl;

public class GeneratedJob implements Comparable {
    private String templateName;
    private String jobName;

    public GeneratedJob(String templateName, String jobName) {
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

    @Override
    public int compareTo(Object o) {
        if (o instanceof GeneratedJob) {
            return jobName.compareTo(((GeneratedJob) o).getJobName());
        } else {
            return jobName.compareTo(o.toString());
        }
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

    @Override
    public String toString() {
        return "GeneratedJob{" +
                "name='" + jobName + "'" +
                (templateName == null ? "" : (", template='" + templateName + "'")) +
                "}";
    }
}
