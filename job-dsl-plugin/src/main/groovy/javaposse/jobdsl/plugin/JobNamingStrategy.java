package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import jenkins.model.Jenkins;

/**
 * A JobNamingStrategy encapsulates where a seed job will look for existing jobs
 * and where it will create new jobs. This matters when you use relative names in
 * a job dsl script and the seed job is in a folder.
 */
public enum JobNamingStrategy {
    /**
     * A naming strategy to provide backwards compatibility for jobs created with
     * plugin versions prior to 1.22.
     */
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
    /**
     * Using this naming strategy jobs with relative path names are created relative
     * to the seed job's parent folder.
     */
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