package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class PhabricatorContext implements Context {
    boolean createCommit
    boolean applyToMaster
    boolean showBuildStartedMessage = true

    /**
     * Create a git commit with the patch. Defaults to {@code false}.
     */
    void createCommit(boolean createCommit = true) {
        this.createCommit = createCommit
    }

    /**
     * If true, always arc patch apply to master. Defaults to {@code false}.
     */
    void applyToMaster(boolean applyToMaster = true) {
        this.applyToMaster = applyToMaster
    }

    /**
     * Shows the 'Build Started:' information message in Phabricator. Defaults to {@code true}.
     */
    void showBuildStartedMessage(boolean showBuildStartedMessage = true) {
        this.showBuildStartedMessage = showBuildStartedMessage
    }
}
