package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class DebianContext implements Context {
    boolean generateChangelog
    String nextVersion
    boolean alwaysBuild
    boolean signPackage = true

    /**
     * Generates a change log.
     */
    void generateChangelog(String nextVersion = null, boolean alwaysBuild = false) {
        this.generateChangelog = true
        this.nextVersion = nextVersion
        this.alwaysBuild = alwaysBuild
    }

    /**
     * If set, the package will be signed. Defaults to {@code true}.
     */
    void signPackage(boolean signPackage = true) {
        this.signPackage = signPackage
    }
}
