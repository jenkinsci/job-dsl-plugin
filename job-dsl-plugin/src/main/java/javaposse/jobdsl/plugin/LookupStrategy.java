package javaposse.jobdsl.plugin;

import hudson.model.Item;
import hudson.model.ItemGroup;
import jenkins.model.Jenkins;
import org.apache.commons.io.FilenameUtils;

import java.util.function.Function;

/**
 * A JobLookupStrategy encapsulates where a seed job will look for existing jobs
 * and where it will create new jobs. This matters when you use relative names in
 * a job dsl script and the seed job is in a folder.
 */
public enum LookupStrategy {
    /**
     * Using this naming strategy jobs with relative path names are absolute names.
     */
    JENKINS_ROOT("Jenkins Root", (seedJob) -> Jenkins.get()),

    /**
     * Using this naming strategy jobs with relative path names are created relative
     * to the seed job's parent folder.
     */
    SEED_JOB("Seed Job", Item::getParent);

    LookupStrategy(String displayName, Function<Item, ItemGroup> context) {
        this.displayName = displayName;
        this.context = context;
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
    public <T extends Item> T getItem(Item seedJob, String path, Class<T> type) {
        String fullName = path.startsWith("/") ? path : getContext(seedJob).getFullName() + "/" + path;
        String normalizePath = normalizePath(fullName);
        if (normalizePath == null) {
            return null;
        }
        return Jenkins.get().getItemByFullName(normalizePath, type);
    }

    /**
     * Get the context in which new items should be created for the given seed job.
     *
     * @param seedJob a seed job
     * @return the context
     */
    protected ItemGroup getContext(Item seedJob) {
        return context.apply(seedJob);
    }

    /**
     * Get the parent {@link hudson.model.ItemGroup} of the item addressed by the given path.
     *
     * @param seedJob the seed job
     * @param path    path to the item for which the parent should be looked up
     * @return parent {@link hudson.model.ItemGroup} of the item with the given path
     */
    public ItemGroup getParent(Item seedJob, String path) {
        Jenkins jenkins = Jenkins.get();

        String absolutePath;
        if (path.startsWith("/")) {
            absolutePath = path.substring(1);
        } else {
            String contextPath = getContext(seedJob).getFullName();
            absolutePath = contextPath.length() == 0 ? path : contextPath + "/" + path;
        }

        int i = absolutePath.lastIndexOf('/');
        if (i > -1) {
            return getItemGroup(absolutePath.substring(0, i));
        } else {
            return jenkins;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    private final String displayName;
    private final Function<Item, ItemGroup> context;

    private static ItemGroup getItemGroup(String path) {
        Jenkins instance = Jenkins.get();
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return null;
        }
        if (normalizedPath.isEmpty() || normalizedPath.equals("/")) {
            return instance;
        }
        Item item = instance.getItemByFullName(normalizedPath);
        return item instanceof ItemGroup ? (ItemGroup) item : null;
    }

    private static String normalizePath(String path) {
        return FilenameUtils.normalize(path, true);
    }
}
