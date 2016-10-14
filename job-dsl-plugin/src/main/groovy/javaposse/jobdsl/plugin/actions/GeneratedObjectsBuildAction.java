package javaposse.jobdsl.plugin.actions;

import jenkins.tasks.SimpleBuildStep;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @deprecated use {@code javaposse.jobdsl.plugin.actions.GeneratedObjectsRunAction} instead
 */
@Deprecated
public abstract class GeneratedObjectsBuildAction<T> implements SimpleBuildStep.LastBuildAction {
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

    @SuppressWarnings("ConstantConditions") // modifiedObjects can be null when this is deserialized by XStream
    public Collection<T> getModifiedObjects() {
        return modifiedObjects == null ? null : new TreeSet<T>(modifiedObjects);
    }
}
