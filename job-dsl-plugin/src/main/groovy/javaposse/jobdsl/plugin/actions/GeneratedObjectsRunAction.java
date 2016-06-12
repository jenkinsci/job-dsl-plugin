package javaposse.jobdsl.plugin.actions;

import java.util.Collection;

abstract class GeneratedObjectsRunAction<T> extends GeneratedObjectsBuildRunAction<T> {
    GeneratedObjectsRunAction(Collection<T> modifiedObjects) {
        super(modifiedObjects);
    }
}
