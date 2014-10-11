package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.Item;
import hudson.model.Run;
import javaposse.jobdsl.dsl.GeneratedJob;
import jenkins.model.RunAction2;

import java.util.Collection;
import java.util.Set;

public class GeneratedJobsBuildAction implements RunAction2 {
    public final Set<GeneratedJob> modifiedJobs;

    private transient AbstractBuild owner;
    private LookupStrategy lookupStrategy = LookupStrategy.JENKINS_ROOT;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs, LookupStrategy lookupStrategy) {
        this.modifiedJobs = Sets.newLinkedHashSet(modifiedJobs);
        this.lookupStrategy = lookupStrategy;
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Items";
    }

    public String getUrlName() {
        return "generatedJobs";
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }

    @Override
    public void onAttached(Run run) {
        if (run instanceof AbstractBuild) {
            owner = (AbstractBuild) run;
        }
    }

    public LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public Collection<GeneratedJob> getModifiedJobs() {
        return modifiedJobs;
    }

    public Set<Item> getItems() {
        Set<Item> result = Sets.newLinkedHashSet();
        if (modifiedJobs != null) {
            for (GeneratedJob job : modifiedJobs) {
                Item item = getLookupStrategy().getItem(owner.getProject(), job.getJobName(), Item.class);
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }
}
