package javaposse.jobdsl.plugin.actions;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class GeneratedObjectsAction<T, B extends GeneratedObjectsBuildAction<T>> implements Action {
    protected final AbstractProject<?, ?> project;
    private final Class<B> buildActionClass;

    GeneratedObjectsAction(AbstractProject<?, ?> project, Class<B> buildActionClass) {
        this.project = project;
        this.buildActionClass = buildActionClass;
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

    public Set<T> findLastGeneratedObjects() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            B action = b.getAction(buildActionClass);
            if (action != null && action.getModifiedObjects() != null) {
                return new LinkedHashSet<T>(action.getModifiedObjects());
            }
        }
        return new LinkedHashSet<T>();
    }

    @SuppressWarnings("unused") // used by some Jelly views
    public Set<T> findAllGeneratedObjects() {
        Set<T> result = new LinkedHashSet<T>();
        for (AbstractBuild build : project.getBuilds()) {
            B action = build.getAction(buildActionClass);
            if (action != null && action.getModifiedObjects() != null) {
                result.addAll(action.getModifiedObjects());
            }
        }
        return result;
    }

    public static <T, A extends GeneratedObjectsAction<T, ?>> Set<T> extractGeneratedObjects(AbstractProject<?, ?> project, Class<A> actionType) {
        A action = project.getAction(actionType);
        return action == null ? new LinkedHashSet<T>() : action.findLastGeneratedObjects();
    }
}
