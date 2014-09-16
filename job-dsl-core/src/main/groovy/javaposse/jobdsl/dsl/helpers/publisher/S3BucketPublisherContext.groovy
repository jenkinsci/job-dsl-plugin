package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class S3BucketPublisherContext implements Context {
    String profile
    List<Node> entries = []
    List<Node> metadata = []

    void profile(String profile) {
        checkArgument(!isNullOrEmpty(profile), 'profile must be specified')
        this.profile = profile
    }

    void entry(String source, String bucketName, Closure closure = null) {
        S3EntryContext context = new S3EntryContext()
        context.source = source
        context.bucket = bucketName
        AbstractContextHelper.executeInContext(closure, context)

        this.entries << NodeBuilder.newInstance().'hudson.plugins.s3.Entry' {
            sourceFile(context.source)
            bucket(context.bucket)
            noUploadOnFailure(context.noUploadOnFailure)
            uploadFromSlave(context.uploadFromSlave)
            managedArtifacts(context.managedArtifacts)
        }
    }

    void metadata(String k, String v) {
        this.metadata << NodeBuilder.newInstance().'hudson.plugins.s3.MetadataPair' {
            key(k)
            value(v)
        }
    }
}
