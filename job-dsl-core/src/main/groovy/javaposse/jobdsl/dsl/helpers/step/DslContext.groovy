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
    def externalScripts = []
    def ignoreExisting = false

    def text(String text) {
        this.scriptText = Preconditions.checkNotNull(text)
    }

    def useScriptText() {
        scriptText.length() > 0
    }

    def external(String... dslScripts) {
        externalScripts.addAll(dslScripts)
    }

    def getTargets() {
        externalScripts.join('\n')
    }

    def ignoreExisting(boolean ignore = true) {
        this.ignoreExisting = ignore
    }

    def removeAction(String action) {

        try {
            this.removedJobAction = DslContext.RemovedJobAction.valueOf(action)
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("removeAction must be one of: ${DslContext.RemovedJobAction.values()}")
        }


    }

}
