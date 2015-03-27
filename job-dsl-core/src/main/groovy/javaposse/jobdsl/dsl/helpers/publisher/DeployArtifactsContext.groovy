package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class DeployArtifactsContext implements Context {
    boolean uniqueVersion = true
    boolean evenIfUnstable

    void uniqueVersion(boolean uniqueVersion = true) {
        this.uniqueVersion = uniqueVersion
    }

    void evenIfUnstable(boolean evenIfUnstable = true) {
        this.evenIfUnstable = evenIfUnstable
    }
}
