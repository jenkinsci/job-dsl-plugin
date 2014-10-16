package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.ContextHelper
import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class S3BucketPublisherContext implements Context {
    private static final List<String> REGIONS = [
            'GovCloud', 'US_EAST_1', 'US_WEST_1', 'US_WEST_2', 'EU_WEST_1', 'AP_SOUTHEAST_1', 'AP_SOUTHEAST_2',
            'AP_NORTHEAST_1', 'SA_EAST_1', 'CN_NORTH_1'
    ]

    List<Node> entries = []
    List<Node> metadata = []

    void entry(String source, String bucketName, String region, Closure closure = null) {
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
