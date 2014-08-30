package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedConfigFile;

import java.util.Collection;
import java.util.Set;

public class GeneratedConfigFilesBuildAction implements Action {
    public final Set<GeneratedConfigFile> modifiedConfigFiles;

    public GeneratedConfigFilesBuildAction(Collection<GeneratedConfigFile> modifiedConfigFiles) {
        this.modifiedConfigFiles = Sets.newLinkedHashSet(modifiedConfigFiles);
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Config Files";
    }

    public String getUrlName() {
        return "generatedConfigFiles";
    }

    public Collection<GeneratedConfigFile> getModifiedConfigFiles() {
        return modifiedConfigFiles;
    }
}
