package javaposse.jobdsl.dsl.helpers.publisher

import hudson.util.VersionNumber
import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class S3BucketPublisherContext extends AbstractContext {
    private static final List<String> REGIONS = [
            'us-gov-west-1', 'us-east-1', 'us-west-1', 'us-west-2', 'eu-west-1', 'eu-central-1', 'ap-southeast-1',
            'ap-southeast-2', 'ap-northeast-1', 'sa-east-1', 'cn-north-1'
    ]

    List<Node> entries = []
    List<Node> metadata = []

    protected S3BucketPublisherContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    void entry(String source, String bucketName, String region, @DslContext(S3EntryContext) Closure closure = null) {
        checkArgument(!isNullOrEmpty(source), 'source must be specified')
        checkArgument(!isNullOrEmpty(bucketName), 'bucket must be specified')

        if (jobManagement.getPluginVersion('s3')?.isOlderThan(new VersionNumber('0.7'))) {
            jobManagement.logDeprecationWarning('support for S3 plugin versions 0.6 and earlier')
        } else {
            checkArgument(REGIONS.contains(region), "region must be one of ${REGIONS.join(', ')}")
        }

        S3EntryContext context = new S3EntryContext()
        ContextHelper.executeInContext(closure, context)

        this.entries << NodeBuilder.newInstance().'hudson.plugins.s3.Entry' {
            sourceFile(source)
            bucket(bucketName)
            storageClass(context.storageClass)
            selectedRegion(region)
            noUploadOnFailure(context.noUploadOnFailure)
            uploadFromSlave(context.uploadFromSlave)
            managedArtifacts(context.managedArtifacts)
        }
    }

    void metadata(String key, String value) {
        this.metadata << NodeBuilder.newInstance().'hudson.plugins.s3.MetadataPair' {
            delegate.key(key)
            delegate.value(value)
        }
    }
}
