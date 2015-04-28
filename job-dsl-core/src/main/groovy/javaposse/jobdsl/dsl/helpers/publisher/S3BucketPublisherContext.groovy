package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class S3BucketPublisherContext implements Context {
    private static final List<String> REGIONS = [
            'GovCloud', 'us-east-1', 'us-west-1', 'us-west-2', 'eu-west-1', 'ap-southeast-1', 'ap-southeast-2',
            'ap-northeast-1', 'sa-east-1', 'cn-north-1'
    ]

    List<Node> entries = []
    List<Node> metadata = []

    void entry(String source, String bucketName, String region, @DslContext(S3EntryContext) Closure closure = null) {
        checkArgument(!isNullOrEmpty(source), 'source must be specified')
        checkArgument(!isNullOrEmpty(bucketName), 'bucket must be specified')
        checkArgument(REGIONS.contains(region), "region must be one of ${REGIONS.join(', ')}")

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
