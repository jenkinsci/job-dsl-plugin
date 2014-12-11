package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import hudson.model.Action;

/**
 * @author ceilfors
 */
public class GeneratedJobAction implements Action {

    AbstractProject<?, ?> templateJob;
    AbstractProject<?, ?> seedJob;

    public GeneratedJobAction(AbstractProject<?, ?> templateJob, AbstractProject<?, ?> seedJob) {
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

    public AbstractProject<?, ?> getTemplateJob() {
        return templateJob;
    }

    public AbstractProject<?, ?> getSeedJob() {
        return seedJob;
    }
}
