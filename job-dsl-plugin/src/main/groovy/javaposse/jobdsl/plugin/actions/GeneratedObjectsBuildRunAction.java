package javaposse.jobdsl.plugin.actions;

import hudson.model.Run;
import jenkins.model.RunAction2;

import java.util.Collection;

/**
 * @deprecated use {@code javaposse.jobdsl.plugin.actions.GeneratedObjectsRunAction} instead
 */
@Deprecated
@SuppressWarnings("Deprecation")
abstract class GeneratedObjectsBuildRunAction<T> extends GeneratedObjectsBuildAction<T> implements RunAction2 {
    protected transient Run owner;

    GeneratedObjectsBuildRunAction(Collection<T> modifiedObjects) {
        super(modifiedObjects);
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }

    @Override
    public void onAttached(Run run) {
        owner = run;
    }
}
