package javaposse.jobdsl.plugin;

import hudson.model.Action;
import hudson.model.Item;
import jenkins.model.Jenkins;

public class SeedJobAction implements Action {
    private final String seedJobName;
    private final String templateJobName;

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

    public Item getSeedJob() {
        return Jenkins.getInstance().getItemByFullName(seedJobName);
    }

    public Item getTemplateJob() {
        return templateJobName == null ? null :
                Jenkins.getInstance().getItemByFullName(templateJobName);
    }
}
