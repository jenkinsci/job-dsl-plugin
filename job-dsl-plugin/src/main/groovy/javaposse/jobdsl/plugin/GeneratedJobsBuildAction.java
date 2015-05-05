package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Item;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.Collection;
import java.util.Set;

public class GeneratedJobsBuildAction extends GeneratedObjectsBuildRunAction<GeneratedJob> {
    @SuppressWarnings("unused")
    private transient Set<GeneratedJob> modifiedJobs;

    private LookupStrategy lookupStrategy = LookupStrategy.JENKINS_ROOT;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs, LookupStrategy lookupStrategy) {
        super(modifiedJobs);
        this.lookupStrategy = lookupStrategy;
    }

    public LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public Set<Item> getItems() {
        Set<Item> result = Sets.newLinkedHashSet();
        for (GeneratedJob job : getModifiedObjects()) {
            Item item = getLookupStrategy().getItem(owner.getProject(), job.getJobName(), Item.class);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    private Object readResolve() {
        return modifiedJobs == null ? this : new GeneratedJobsBuildAction(modifiedJobs, lookupStrategy);
    }
}
