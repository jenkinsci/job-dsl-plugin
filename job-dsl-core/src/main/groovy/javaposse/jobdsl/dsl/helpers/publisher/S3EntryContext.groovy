package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

class S3EntryContext extends AbstractContext {
    private static final List<String> STORAGE_CLASSES = ['STANDARD', 'REDUCED_REDUNDANCY']

    String storageClass = 'STANDARD'
    boolean noUploadOnFailure = false
    boolean uploadFromSlave = false
    boolean managedArtifacts = false
    boolean useServerSideEncryption = false
    boolean flatten = false

    protected S3EntryContext(JobManagement jobManagement) {
        super(jobManagement)
    }

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

    /**
     * @since 1.38
     */
    @RequiresPlugin(id = 's3', minimumVersion = '0.7')
    void useServerSideEncryption(boolean useServerSideEncryption = true) {
        this.useServerSideEncryption = useServerSideEncryption
    }

    /**
     * @since 1.38
     */
    @RequiresPlugin(id = 's3', minimumVersion = '0.7')
    void flatten(boolean flatten = true) {
        this.flatten = flatten
    }
}
