package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedConfigFile;
import javaposse.jobdsl.dsl.GeneratedUserContent;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

public class GeneratedUserContentsAction implements Action {
    AbstractProject<?, ?> project;

    public GeneratedUserContentsAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "generatedUserContents";
    }

    public Set<GeneratedUserContent> findLastGeneratedUserContents() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            GeneratedUserContentsBuildAction action = b.getAction(GeneratedUserContentsBuildAction.class);
            if (action != null && action.getModifiedUserContents() != null) {
                return newLinkedHashSet(action.getModifiedUserContents());
            }
        }
        return newLinkedHashSet();
    }

    public Set<GeneratedUserContent> findAllGeneratedUserContents() {
        Set<GeneratedUserContent> allGeneratedUserContents = Sets.newLinkedHashSet();
        for (AbstractBuild build : project.getBuilds()) {
            GeneratedUserContentsBuildAction ret = build.getAction(GeneratedUserContentsBuildAction.class);
            if (ret != null && ret.getModifiedUserContents() != null) {
                allGeneratedUserContents.addAll(ret.getModifiedUserContents());
            }
        }
        return allGeneratedUserContents;
    }
}
