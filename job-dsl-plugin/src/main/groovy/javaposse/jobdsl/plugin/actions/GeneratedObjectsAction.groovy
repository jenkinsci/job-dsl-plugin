package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Job
import hudson.model.Run

public abstract class GeneratedObjectsAction<T, B extends GeneratedObjectsBuildRunAction<T>> implements Action {
    protected final Job<?, ?> job;
    private final Class<B> buildActionClass;

    GeneratedObjectsAction(Job<?, ?> job, Class<B> buildActionClass) {
        this.job = job;
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
        for (Run run = job.getLastBuild(); run != null; run = run.getPreviousBuild()) {
            B action = run.getAction(buildActionClass);
            if (action != null && action.getModifiedObjects() != null) {
                return new LinkedHashSet<T>(action.getModifiedObjects());
            }
        }
        return new LinkedHashSet<T>();
    }

    @SuppressWarnings("unused") // used by some Jelly views
    public Set<T> findAllGeneratedObjects() {
        Set<T> result = new LinkedHashSet<T>();
        for (Run run : job.getBuilds()) {
            B action = run.getAction(buildActionClass);
            if (action != null && action.getModifiedObjects() != null) {
                result.addAll(action.getModifiedObjects());
            }
        }
        return result;
    }

    public static <T, A extends GeneratedObjectsAction<T, ?>> Set<T> extractGeneratedObjects(Job job, Class<A> actionType) {
        A action = job.getAction(actionType);
        return action == null ? new LinkedHashSet<T>() : action.findLastGeneratedObjects();
    }
}
