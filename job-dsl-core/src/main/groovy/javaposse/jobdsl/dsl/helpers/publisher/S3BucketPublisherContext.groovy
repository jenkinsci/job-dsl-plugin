package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class S3BucketPublisherContext extends AbstractContext {
    private static final List<String> REGIONS = [
            'us-gov-west-1', 'us-east-1', 'us-west-1', 'us-west-2', 'eu-west-1', 'eu-central-1', 'ap-south-1',
            'ap-southeast-1', 'ap-southeast-2', 'ap-northeast-1', 'ap-northeast-2', 'sa-east-1', 'cn-north-1'
    ]

    List<Node> entries = []
    List<Node> metadata = []

    protected S3BucketPublisherContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies files to upload. Can be called multiple times to add upload more files.
     */
    void entry(String source, String bucketName, String region, @DslContext(S3EntryContext) Closure closure = null) {
        checkNotNullOrEmpty(source, 'source must be specified')
        checkNotNullOrEmpty(bucketName, 'bucket must be specified')
        checkArgument(REGIONS.contains(region), "region must be one of ${REGIONS.join(', ')}")

        S3EntryContext context = new S3EntryContext(jobManagement)
        ContextHelper.executeInContext(closure, context)

        this.entries << NodeBuilder.newInstance().'hudson.plugins.s3.Entry' {
            sourceFile(source)
            bucket(bucketName)
            storageClass(context.storageClass)
            selectedRegion(region)
            noUploadOnFailure(context.noUploadOnFailure)
            uploadFromSlave(context.uploadFromSlave)
            managedArtifacts(context.managedArtifacts)
            useServerSideEncryption(context.useServerSideEncryption)
            flatten(context.flatten)
        }
    }

    /**
     * Adds metadata for the upload files. Can be called multiple times to add more metadata.
     */
    void metadata(String key, String value) {
        this.metadata << NodeBuilder.newInstance().'hudson.plugins.s3.MetadataPair' {
            delegate.key(key)
            delegate.value(value)
        }
    }
}
