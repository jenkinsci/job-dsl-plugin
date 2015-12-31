package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions

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

    /**
     * Specifies the S3 storage class. Must be either {@code 'STANDARD'} (default) or {@code 'REDUCED_REDUNDANCY'}.
     */
    void storageClass(String storageClass) {
        Preconditions.checkArgument(
                STORAGE_CLASSES.contains(storageClass),
                "storageClass must be one of ${STORAGE_CLASSES.join(', ')}"
        )

        this.storageClass = storageClass
    }

    /**
     * Skips upload if the build failed. Defaults to {@code false}.
     */
    void noUploadOnFailure(boolean noUploadOnFailure = true) {
        this.noUploadOnFailure = noUploadOnFailure
    }

    /**
     * Upload directly from the slave, instead of proxying the upload to the master. Defaults to {@code false}.
     */
    void uploadFromSlave(boolean uploadFromSlave = true) {
        this.uploadFromSlave = uploadFromSlave
    }

    /**
     * If set, Jenkins fully manages the artifacts, exactly like it does when the artifacts are published to the master.
     * Defaults to {@code false}.
     */
    void managedArtifacts(boolean managedArtifacts = true) {
        this.managedArtifacts = managedArtifacts
    }

    /**
     * Enables S3 AES-256 server side encryption support. Defaults to {@code false}.
     *
     * @since 1.38
     */
    void useServerSideEncryption(boolean useServerSideEncryption = true) {
        this.useServerSideEncryption = useServerSideEncryption
    }

    /**
     * If set, ignores the directory structure of the artifacts in the source project and copies all matching artifacts
     * directly into the specified bucket.
     *
     * @since 1.38
     */
    void flatten(boolean flatten = true) {
        this.flatten = flatten
    }
}
