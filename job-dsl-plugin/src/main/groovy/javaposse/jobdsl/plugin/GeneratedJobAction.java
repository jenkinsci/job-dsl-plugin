package javaposse.jobdsl.plugin;

import hudson.model.Action;
import hudson.model.Job;

/**
 * @author ceilfors
 */
public class GeneratedJobAction implements Action {

    Job<?, ?> templateJob;
    Job<?, ?> seedJob;

    public GeneratedJobAction(Job<?, ?> templateJob, Job<?, ?> seedJob) {
        this.templateJob = templateJob;
        this.seedJob = seedJob;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Generated job:";
    }

    @Override
    public String getUrlName() {
        return "generatedJob";
    }

    public Job<?, ?> getTemplateJob() {
        return templateJob;
    }

    public Job<?, ?> getSeedJob() {
        return seedJob;
    }
}
