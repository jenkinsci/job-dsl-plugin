package javaposse.jobdsl.plugin.actions;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import jenkins.model.RunAction2;

import java.util.Collection;

abstract class GeneratedObjectsBuildRunAction<T> extends GeneratedObjectsBuildAction<T> implements RunAction2 {
    transient AbstractBuild owner;

    GeneratedObjectsBuildRunAction(Collection<T> modifiedObjects) {
        super(modifiedObjects);
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }

    @Override
    public void onAttached(Run run) {
        if (run instanceof AbstractBuild) {
            owner = (AbstractBuild) run;
        }
    }
}
