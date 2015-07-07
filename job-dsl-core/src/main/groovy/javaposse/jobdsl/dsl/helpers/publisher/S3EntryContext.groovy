package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class S3EntryContext implements Context {
    private static final List<String> STORAGE_CLASSES = ['STANDARD', 'REDUCED_REDUNDANCY']

    String storageClass = 'STANDARD'
    boolean noUploadOnFailure = false
    boolean uploadFromSlave = false
    boolean managedArtifacts = false

    void storageClass(String storageClass) {
        Preconditions.checkArgument(
                STORAGE_CLASSES.contains(storageClass),
                "storageClass must be one of ${STORAGE_CLASSES.join(', ')}"
        )

        this.storageClass = storageClass
    }

    void noUploadOnFailure(boolean noUploadOnFailure = true) {
        this.noUploadOnFailure = noUploadOnFailure
    }

    void uploadFromSlave(boolean uploadFromSlave = true) {
        this.uploadFromSlave = uploadFromSlave
    }

    void managedArtifacts(boolean managedArtifacts = true) {
        this.managedArtifacts = managedArtifacts
    }
}
