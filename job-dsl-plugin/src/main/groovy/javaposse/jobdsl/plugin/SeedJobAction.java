package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import hudson.model.Action;

public class SeedJobAction implements Action {

    AbstractProject<?, ?> templateJob;
    AbstractProject<?, ?> seedJob;

    public SeedJobAction(AbstractProject<?, ?> templateJob, AbstractProject<?, ?> seedJob) {
        this.templateJob = templateJob;
        this.seedJob = seedJob;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Seed job:";
    }

    @Override
    public String getUrlName() {
        return "seedJob";
    }

    public AbstractProject<?, ?> getTemplateJob() {
        return templateJob;
    }

    public AbstractProject<?, ?> getSeedJob() {
        return seedJob;
    }
}
