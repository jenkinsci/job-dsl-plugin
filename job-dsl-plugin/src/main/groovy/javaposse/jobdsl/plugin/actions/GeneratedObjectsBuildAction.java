package javaposse.jobdsl.plugin.actions;

import hudson.model.Action;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class GeneratedObjectsBuildAction<T> implements Action {
    private final Set<T> modifiedObjects;

    GeneratedObjectsBuildAction(Collection<T> modifiedObjects) {
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

    public Collection<T> getModifiedObjects() {
        return modifiedObjects == null ? new LinkedHashSet<T>() : modifiedObjects;
    }
}
