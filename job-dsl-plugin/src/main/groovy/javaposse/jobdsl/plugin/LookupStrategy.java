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
        public <T extends Item> T getItem(Item seedJob, String path, Class<T> type) {
            return Jenkins.getInstance().getItemByFullName(path, type);
        }

        @Override
        protected ItemGroup getContext(Item seedJob) {
            return Jenkins.getInstance();
        }
    },

    /**
     * Using this naming strategy jobs with relative path names are created relative
     * to the seed job's parent folder.
     */
    SEED_JOB("Seed Job") {
        @Override
        public <T extends Item> T getItem(Item seedJob, String path, Class<T> type) {
            String fullName = path.startsWith("/") ? path : seedJob.getParent().getFullName() + "/" + path;
            return Jenkins.getInstance().getItemByFullName(fullName, type);
        }

        @Override
        protected ItemGroup getContext(Item seedJob) {
            return seedJob.getParent();
        }
    };

    LookupStrategy(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get an item by its path name in the context of a seed job.
     *
     * @param seedJob the seed job
     * @param path    the path name
     * @param type    the type of the item
     * @param <T>     the type of the item
     * @return the item for the given path
     */
    public abstract <T extends Item> T getItem(Item seedJob, String path, Class<T> type);

    /**
     * Get the context in which new items should be created for the given seed job.
     *
     * @param seedJob a seed job
     * @return the context
     */
    protected abstract ItemGroup getContext(Item seedJob);

    /**
     * Get the parent {@link hudson.model.ItemGroup} of the item addressed by the given path.
     *
     * @param seedJob the seed job
     * @param path    path to the item for which the parent should be looked up
     * @return parent {@link hudson.model.ItemGroup} of the item with the given path
     */
    public ItemGroup getParent(Item seedJob, String path) {
        Jenkins jenkins = Jenkins.getInstance();
        int i = path.lastIndexOf('/');
        switch (i) {
            case -1:
                return getContext(seedJob);
            case 0:
                return jenkins;
            default:
                Item item = jenkins.getItem(path.substring(0, i), getContext(seedJob));
                return item instanceof ItemGroup ? (ItemGroup) item : null;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    private final String displayName;
}
