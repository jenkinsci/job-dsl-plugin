package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.GeneratedConfigFile;

import java.util.Collection;
import java.util.Set;

public class GeneratedConfigFilesBuildAction extends GeneratedObjectsBuildAction<GeneratedConfigFile> {
    @SuppressWarnings("unused")
    private transient Set<GeneratedConfigFile> modifiedConfigFiles;

    public GeneratedConfigFilesBuildAction(Collection<GeneratedConfigFile> modifiedConfigFiles) {
        super(modifiedConfigFiles);
    }

    public String getDisplayName() {
        return "Generated Config Files";
    }

    public String getUrlName() {
        return "generatedConfigFiles";
    }

    @SuppressWarnings("unused")
    private Object readResolve() {
        return modifiedConfigFiles == null ? this : new GeneratedConfigFilesBuildAction(modifiedConfigFiles);
    }
}
