package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class DeployArtifactsContext implements Context {
    boolean uniqueVersion = true
    boolean evenIfUnstable

    /**
     * If set, assigns timestamp-based unique version number to the deployed artifacts, when their versions end with
     * {@code -SNAPSHOT}. Defaults to {@code true}.
     */
    void uniqueVersion(boolean uniqueVersion = true) {
        this.uniqueVersion = uniqueVersion
    }

    /**
     * If set, deploys even if the build is unstable. Defaults to {@code false}.
     */
    void evenIfUnstable(boolean evenIfUnstable = true) {
        this.evenIfUnstable = evenIfUnstable
    }
}
