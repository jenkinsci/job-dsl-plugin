package javaposse.jobdsl.plugin;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import jenkins.model.RunAction2;

import java.util.Collection;

public abstract class GeneratedObjectsBuildRunAction<T> extends GeneratedObjectsBuildAction<T> implements RunAction2 {
    protected transient AbstractBuild owner;

    public GeneratedObjectsBuildRunAction(Collection<T> modifiedObjects) {
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
