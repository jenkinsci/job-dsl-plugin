package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class DebianContext implements Context {
    boolean generateChangelog
    String nextVersion
    boolean alwaysBuild
    boolean signPackage = true

    void generateChangelog(String nextVersion = null, boolean alwaysBuild = false) {
        this.generateChangelog = true
        this.nextVersion = nextVersion
        this.alwaysBuild = alwaysBuild
    }

    void signPackage(boolean signPackage = true) {
        this.signPackage = signPackage
    }
}
