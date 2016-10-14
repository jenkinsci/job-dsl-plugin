package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Job
import hudson.model.Run

abstract class GeneratedObjectsAction<T, B extends GeneratedObjectsRunAction<T>> implements Action {
    protected final Job<? extends Job, ? extends Run> job
    private final Class<B> buildActionClass

    final String iconFileName = null
    final String displayName = null
    final String urlName = null

    protected GeneratedObjectsAction(Job job, Class buildActionClass) {
        this.job = job
        this.buildActionClass = buildActionClass
    }

    Set<T> findLastGeneratedObjects() {
        for (Run run = job.lastBuild; run != null; run = run.previousBuild) {
            B action = run.getAction(buildActionClass)
            if (action != null && action.modifiedObjects != null) {
                return action.modifiedObjects
            }
        }
        []
    }

    @SuppressWarnings('GroovyUnusedDeclaration') // used by some Jelly views
    Set<T> findAllGeneratedObjects() {
        Set<T> result = [] as SortedSet<T>
        for (Run run : job.builds) {
            B action = run.getAction(buildActionClass)
            if (action != null && action.modifiedObjects != null) {
                result.addAll(action.modifiedObjects)
            }
        }
        result
    }

    static
    <T, B extends GeneratedObjectsRunAction<T>, A extends GeneratedObjectsAction<T, B>> Set<T> extractGeneratedObjects(
            Job job, Class<A> actionType) {
        A action = job.getAction(actionType)
        action == null ? [] : action.findLastGeneratedObjects()
    }
}
