package javaposse.jobdsl.dsl.helpers.publisher

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class S3BucketPublisherContext extends AbstractContext {
    /**
     * This has to match com.amazonaws.regions.Regions enum:
     * http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/regions/Regions.html
     */
    private static final List<String> REGIONS = [
        'AP_NORTHEAST_1',
        'AP_SOUTHEAST_1',
        'AP_SOUTHEAST_2',
        'CN_NORTH_1',
        'EU_CENTRAL_1',
        'EU_WEST_1',
        'GovCloud',
        'SA_EAST_1',
        'US_EAST_1',
        'US_WEST_1',
        'US_WEST_2'
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

        if (!jobManagement.getPluginVersion('s3')?.isOlderThan(new VersionNumber('0.7'))) {
            checkArgument(REGIONS.contains(region), "region must be one of ${REGIONS.join(', ')}")
        }

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

            if (!jobManagement.getPluginVersion('s3')?.isOlderThan(new VersionNumber('0.7'))) {
                useServerSideEncryption(context.useServerSideEncryption)
                flatten(context.flatten)
            }
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
