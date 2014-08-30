package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedConfigFile;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

public class GeneratedConfigFilesAction implements Action {
    AbstractProject<?, ?> project;

    public GeneratedConfigFilesAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "generatedConfigFiles";
    }

    public Set<GeneratedConfigFile> findLastGeneratedConfigFiles() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            GeneratedConfigFilesBuildAction action = b.getAction(GeneratedConfigFilesBuildAction.class);
            if (action != null && action.getModifiedConfigFiles() != null) {
                return newLinkedHashSet(action.getModifiedConfigFiles());
            }
        }
        return newLinkedHashSet();
    }

    public Set<GeneratedConfigFile> findAllGeneratedConfigFiles() {
        Set<GeneratedConfigFile> allGeneratedConfigFiles = Sets.newLinkedHashSet();
        for (AbstractBuild build : project.getBuilds()) {
            GeneratedConfigFilesBuildAction ret = build.getAction(GeneratedConfigFilesBuildAction.class);
            if (ret != null && ret.getModifiedConfigFiles() != null) {
                allGeneratedConfigFiles.addAll(ret.getModifiedConfigFiles());
            }
        }
        return allGeneratedConfigFiles;
    }
}
