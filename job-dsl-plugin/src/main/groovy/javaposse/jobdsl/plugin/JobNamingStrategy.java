package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import jenkins.model.Jenkins;

public enum JobNamingStrategy {
    JENKINS_ROOT("Jenkins Root") {
        @Override
        public Item getItem(String pathName, AbstractProject<?,?> seedJob) {
            return Jenkins.getInstance().getItemByFullName(pathName, AbstractProject.class);
        }

        @Override
        public ItemGroup getBase(AbstractProject<?, ?> seedJob) {
            return Jenkins.getInstance();
        }
    },
    SEED_JOB("Seed Job") {
        @Override
        public Item getItem(String pathName, AbstractProject<?,?> seedJob) {
            return Jenkins.getInstance().getItem(pathName, seedJob);
        }

        @Override
        public ItemGroup getBase(AbstractProject<?, ?> seedJob) {
            return seedJob.getParent();
        }
    };

    String displayName;

    JobNamingStrategy(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public abstract Item getItem(String pathName, AbstractProject<?,?> seedJob);
    public abstract ItemGroup getBase(AbstractProject<?, ?> seedJob);
}