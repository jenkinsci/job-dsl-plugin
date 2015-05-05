package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.Action;

import java.util.Collection;
import java.util.Set;

public abstract class GeneratedObjectsBuildAction<T> implements Action {
    private final Set<T> modifiedObjects;

    public GeneratedObjectsBuildAction(Collection<T> modifiedObjects) {
        this.modifiedObjects = Sets.newLinkedHashSet(modifiedObjects);
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
        return modifiedObjects == null ? Sets.<T>newLinkedHashSet() : modifiedObjects;
    }
}
