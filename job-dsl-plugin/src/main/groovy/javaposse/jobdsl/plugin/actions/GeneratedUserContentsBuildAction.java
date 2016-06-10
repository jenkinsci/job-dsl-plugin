package javaposse.jobdsl.plugin.actions;

import hudson.model.Action;
import javaposse.jobdsl.dsl.GeneratedUserContent;

import java.util.Collection;
import java.util.Collections;

public class GeneratedUserContentsBuildAction extends GeneratedObjectsRunAction<GeneratedUserContent> {
    public GeneratedUserContentsBuildAction(Collection<GeneratedUserContent> modifiedUserContents) {
        super(modifiedUserContents);
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Collections.singleton(new GeneratedUserContentsAction(owner.getParent()));
    }
}
