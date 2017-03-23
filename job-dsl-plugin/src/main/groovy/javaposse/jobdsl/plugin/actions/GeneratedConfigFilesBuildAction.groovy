package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import javaposse.jobdsl.dsl.GeneratedConfigFile

class GeneratedConfigFilesBuildAction extends GeneratedObjectsRunAction<GeneratedConfigFile> {
    @SuppressWarnings('UnnecessaryTransientModifier')
    @Deprecated
    private transient Set<GeneratedConfigFile> modifiedConfigFiles

    GeneratedConfigFilesBuildAction(Collection<GeneratedConfigFile> modifiedConfigFiles) {
        super(modifiedConfigFiles)
    }

    @Override
    Collection<? extends Action> getProjectActions() {
        Collections.singleton(new GeneratedConfigFilesAction(owner.parent))
    }

    @SuppressWarnings(['UnusedPrivateMethod', 'GroovyUnusedDeclaration'])
    private Object readResolve() {
        modifiedObjects == null ? new GeneratedConfigFilesBuildAction(modifiedConfigFiles) : this
    }
}
