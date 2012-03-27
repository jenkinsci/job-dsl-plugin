package javaposse.jobdsl;

import hudson.model.Action;
import java.util.List;


public class GeneratedJobsAction implements Action {
    List<String> jobNames;

    public GeneratedJobsAction(List<String> jobNames) {
        this.jobNames = jobNames;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "generatedFiles";
    }

    public List<String> getJobNames() {
        return jobNames;
    }
}
