package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context

class ArtifactDeployerContext implements Context {
    String includes
    String baseDir
    String remoteFileLocation
    String excludes
    boolean flatten
    boolean cleanUp
    boolean deleteRemoteArtifacts
    String deleteRemoteArtifactsByScript
    boolean failIfNoFiles

    /**
     * Specifies the artifacts to deploy using an Ant-style pattern.
     */
    void includes(String includes) {
        this.includes = includes
    }

    /**
     * Sets the base directory to deploy from.
     */
    void baseDir(String baseDir) {
        this.baseDir = baseDir
    }

    /**
     * Specifies the output directory to which the artifacts will be deployed to.
     */
    void remoteFileLocation(String remoteFileLocation) {
        this.remoteFileLocation = remoteFileLocation
    }

    /**
     * Specifies the artifacts to exclude from deployment using an Ant-style pattern.
     */
    void excludes(String excludes) {
        this.excludes = excludes
    }

    /**
     * If set, ignores the directory structure of the source files. Defaults to {@code false}.
     */
    void flatten(boolean flatten = true) {
        this.flatten = flatten
    }

    /**
     * If set, deletes the output directory before deploying artifacts. Defaults to {@code false}.
     */
    void cleanUp(boolean cleanUp = true) {
        this.cleanUp = cleanUp
    }

    /**
     * If set, deletes remote artifacts when the build is deleted. Defaults to {@code false}.
     */
    void deleteRemoteArtifacts(boolean deleteRemoteArtifacts = true) {
        this.deleteRemoteArtifacts = deleteRemoteArtifacts
    }

    /**
     * Executes a Groovy script before a build is deleted.
     */
    void deleteRemoteArtifactsByScript(String script) {
        this.deleteRemoteArtifactsByScript = script
    }

    /**
     * If set, fails the build if there are no files to deploy. Defaults to {@code false}.
     */
    void failIfNoFiles(boolean failIfNoFiles = true) {
        this.failIfNoFiles = failIfNoFiles
    }
}
