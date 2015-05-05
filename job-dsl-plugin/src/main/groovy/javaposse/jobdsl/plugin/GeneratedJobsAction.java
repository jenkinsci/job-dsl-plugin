package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Item;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.Set;

public class GeneratedJobsAction extends GeneratedObjectsAction<GeneratedJob, GeneratedJobsBuildAction> {
    public GeneratedJobsAction(AbstractProject<?, ?> project) {
        super(project, GeneratedJobsBuildAction.class);
    }

    public Set<Item> getItems() {
        Set<Item> result = Sets.newLinkedHashSet();
        for (AbstractBuild build : project.getBuilds()) {
            GeneratedJobsBuildAction ret = build.getAction(GeneratedJobsBuildAction.class);
            if (ret != null) {
                result.addAll(ret.getItems());
            }
        }
        return result;
    }
}
