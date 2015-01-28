package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class S3BucketPublisherContext implements Context {

    List<Node> entries = []
    List<Node> metadata = []

    void entry(String source, String bucketName, String region, @DslContext(S3EntryContext) Closure closure = null) {
        checkArgument(!isNullOrEmpty(source), 'source must be specified')
        checkArgument(!isNullOrEmpty(bucketName), 'bucket must be specified')
        checkArgument(!isNullOrEmpty(region), 'region must be specified')

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
