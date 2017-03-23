package javaposse.jobdsl.plugin.actions;

import hudson.model.Run;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class GeneratedObjectsRunAction<T> implements RunAction2, SimpleBuildStep.LastBuildAction {
    private final Set<T> modifiedObjects;
    protected transient Run owner;

    GeneratedObjectsRunAction(Collection<T> modifiedObjects) {
        this.modifiedObjects = new LinkedHashSet<T>(modifiedObjects);
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }

    @Override
    public void onAttached(Run run) {
        owner = run;
    }

    @SuppressWarnings("ConstantConditions") // modifiedObjects can be null when this is deserialized by XStream
    public Collection<T> getModifiedObjects() {
        return modifiedObjects == null ? null : new TreeSet<T>(modifiedObjects);
    }
}
