package javaposse.jobdsl.plugin;

import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.ItemGroup;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

/**
 * A JobLookupStrategy encapsulates where a seed job will look for existing jobs
 * and where it will create new jobs. This matters when you use relative names in
 * a job dsl script and the seed job is in a folder.
 */
public enum JobLookupStrategy {
    /**
     * A naming strategy to provide backwards compatibility for jobs created with
     * plugin versions prior to 1.23.
     */
    JENKINS_ROOT("Jenkins Root") {
        @Override
        public Item getItem(AbstractProject<?, ?> seedJob, String pathName) {
            return Jenkins.getInstance().getItemByFullName(pathName, AbstractProject.class);
        }

        @Override
        public ItemGroup getContext(AbstractProject<?, ?> seedJob) {
            return Jenkins.getInstance();
        }
    },
    /**
     * Using this naming strategy jobs with relative path names are created relative
     * to the seed job's parent folder.
     */
    SEED_JOB("Seed Job") {
        @Override
        public Item getItem(AbstractProject<?, ?> seedJob, String pathName) {
            // Don't use Jenkins#getItem(String, Item) here because it will fallback to resolving
            // items against Jenkins root when an item could not be found. For the Job DSL plugin it's
            // better to be precise and don't allow this fallback.
            if (StringUtils.isEmpty(pathName)) {
                return null;
            } else if (pathName.startsWith("/")) {
                return Jenkins.getInstance().getItemByFullName(pathName);
            } else {
                return Jenkins.getInstance().getItemByFullName(seedJob.getParent().getFullName() + "/" + pathName);
            }
        }

        @Override
        public ItemGroup getContext(AbstractProject<?, ?> seedJob) {
            return seedJob.getParent();
        }
    };

    String displayName;

    JobLookupStrategy(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get an item by its path name in the context of a seed job.
     *
     * @param seedJob the seed job
     * @param pathName the path name
     * @return the item for the given path
     */
    public abstract Item getItem(AbstractProject<?, ?> seedJob, String pathName);

    /**
     * Get the context in which new jobs should be created for the given seed job.
     * @param seedJob a seed job
     * @return the context
     */
    public abstract ItemGroup getContext(AbstractProject<?, ?> seedJob);
}