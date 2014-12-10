package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import hudson.model.Action;
import jenkins.model.Jenkins;

public class SeedJobAction implements Action {

    String templateJobName;
    String seedJobName;

    public SeedJobAction(String seedJobName, String templateJobName) {
        this.seedJobName = seedJobName;
        this.templateJobName = templateJobName;
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
        return templateJobName == null ? null :
                Jenkins.getInstance().getItemByFullName(templateJobName, AbstractProject.class);
    }

    public AbstractProject<?, ?> getSeedJob() {
        return Jenkins.getInstance().getItemByFullName(seedJobName, AbstractProject.class);
    }
}
