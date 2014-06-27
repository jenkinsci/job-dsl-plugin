package javaposse.jobdsl.plugin;

import hudson.model.Item;
import hudson.model.ItemGroup;
import jenkins.model.Jenkins;

/**
 * A JobLookupStrategy encapsulates where a seed job will look for existing jobs
 * and where it will create new jobs. This matters when you use relative names in
 * a job dsl script and the seed job is in a folder.
 */
public enum LookupStrategy {
    /**
     * Using this naming strategy jobs with relative path names are absolute names.
     */
    JENKINS_ROOT("Jenkins Root") {
        @Override
        public <T extends Item> T getItem(Item seedJob, String pathName, Class<T> type) {
            return Jenkins.getInstance().getItemByFullName(pathName, type);
        }

        @Override
        public ItemGroup getContext(Item seedJob) {
            return Jenkins.getInstance();
        }
    },

    /**
     * Using this naming strategy jobs with relative path names are created relative
     * to the seed job's parent folder.
     */
    SEED_JOB("Seed Job") {
        @Override
        public <T extends Item> T getItem(Item seedJob, String pathName, Class<T> type) {
            return Jenkins.getInstance().getItem(pathName, seedJob.getParent(), type);
        }

        @Override
        public ItemGroup getContext(Item seedJob) {
            return seedJob.getParent();
        }
    };

    LookupStrategy(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get an item by its path name in the context of a seed job.
     *
     * @param seedJob  the seed job
     * @param pathName the path name
     * @return the item for the given path
     */
    public abstract <T extends Item> T getItem(Item seedJob, String pathName, Class<T> type);

    /**
     * Get the context in which new jobs should be created for the given seed job.
     *
     * @param seedJob a seed job
     * @return the context
     */
    public abstract ItemGroup getContext(Item seedJob);

    public String getDisplayName() {
        return displayName;
    }

    private final String displayName;
}
