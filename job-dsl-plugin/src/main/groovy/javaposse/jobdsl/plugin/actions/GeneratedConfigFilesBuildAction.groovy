package javaposse.jobdsl.plugin.actions;

import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedConfigFile;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class GeneratedConfigFilesBuildAction extends GeneratedObjectsRunAction<GeneratedConfigFile> {
    @SuppressWarnings("unused")
    private transient Set<GeneratedConfigFile> modifiedConfigFiles;

    public GeneratedConfigFilesBuildAction(Collection<GeneratedConfigFile> modifiedConfigFiles) {
        super(modifiedConfigFiles);
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Collections.singleton(new GeneratedConfigFilesAction(owner.getParent()));
    }

    @SuppressWarnings("unused")
    private Object readResolve() {
        return modifiedConfigFiles == null ? this : new GeneratedConfigFilesBuildAction(modifiedConfigFiles);
    }
}
