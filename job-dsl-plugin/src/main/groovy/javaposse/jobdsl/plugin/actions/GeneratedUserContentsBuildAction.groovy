package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import javaposse.jobdsl.dsl.GeneratedUserContent

class GeneratedUserContentsBuildAction extends GeneratedObjectsRunAction<GeneratedUserContent> {
    GeneratedUserContentsBuildAction(Collection<GeneratedUserContent> modifiedUserContents) {
        super(modifiedUserContents)
    }

    @Override
    Collection<? extends Action> getProjectActions() {
        Collections.singleton(new GeneratedUserContentsAction(owner.parent))
    }
}
