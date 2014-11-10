package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

class DslContext implements Context {

    enum RemovedJobAction {
        IGNORE,
        DISABLE,
        DELETE
    }

    String scriptText = ''
    DslContext.RemovedJobAction removedJobAction = DslContext.RemovedJobAction.IGNORE
    List<String> externalScripts = []
    boolean ignoreExisting = false

    void text(String text) {
        this.scriptText = Preconditions.checkNotNull(text)
    }

    boolean useScriptText() {
        scriptText.length() > 0
    }

    void external(String... dslScripts) {
        externalScripts.addAll(dslScripts)
    }

    String getTargets() {
        externalScripts.join('\n')
    }

    void ignoreExisting(boolean ignore = true) {
        this.ignoreExisting = ignore
    }

    void removeAction(String action) {
        try {
            this.removedJobAction = DslContext.RemovedJobAction.valueOf(action)
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("removeAction must be one of: ${DslContext.RemovedJobAction.values()}")
        }
    }
}
