package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

public abstract class GeneratedObjectsAction<T, B extends GeneratedObjectsBuildAction<T>> implements Action {
    protected final AbstractProject<?, ?> project;
    private final Class<B> buildActionClass;

    public GeneratedObjectsAction(AbstractProject<?, ?> project, Class<B> buildActionClass) {
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
                return newLinkedHashSet(action.getModifiedObjects());
            }
        }
        return newLinkedHashSet();
    }

    public Set<T> findAllGeneratedObjects() {
        Set<T> result = Sets.newLinkedHashSet();
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
        return action == null ? Sets.<T>newLinkedHashSet() : action.findAllGeneratedObjects();
    }
}
