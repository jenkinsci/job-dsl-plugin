package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context

class DebianContext implements Context {
    String nextVersion
    boolean generateChangelog
    boolean signPackage
    boolean buildEvenWhenThereAreNoChanges

    void nextVersion(String nextVersion) {
        this.nextVersion = nextVersion
    }

    void generateChangelog(boolean generateChangelog) {
        this.generateChangelog = generateChangelog
    }

    void signPackage(boolean signPackage) {
        this.signPackage = signPackage
    }

    void buildEvenWhenThereAreNoChanges(boolean buildEvenWhenThereAreNoChanges) {
        this.buildEvenWhenThereAreNoChanges = buildEvenWhenThereAreNoChanges
    }

}
