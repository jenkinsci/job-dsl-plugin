package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class S3EntryContext implements Context {
    String source
    String bucket
    Boolean noUploadOnFailure = false
    Boolean uploadFromSlave = false
    Boolean managedArtifacts = false

    void source(String source) {
        checkArgument(!isNullOrEmpty(source), 'source must be specified')
        this.source = source
    }

    void bucket(String bucket) {
        checkArgument(!isNullOrEmpty(bucket), 'bucket must be specified')
        this.bucket = bucket
    }

    void noUploadOnFailure(Boolean noUploadOnFailure = true) {
        this.noUploadOnFailure = noUploadOnFailure
    }

    void uploadFromSlave(Boolean uploadFromSlave = true) {
        this.uploadFromSlave = uploadFromSlave
    }

    void managedArtifacts(Boolean managedArtifacts = true) {
        this.managedArtifacts = managedArtifacts
    }

}
