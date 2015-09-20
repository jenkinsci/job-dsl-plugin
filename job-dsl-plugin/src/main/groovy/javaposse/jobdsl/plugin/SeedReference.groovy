package javaposse.jobdsl.plugin

import groovy.transform.EqualsAndHashCode

/**
 * Bean to record a reference from a generated job to the seed job and optionally to the template job.
 */
@EqualsAndHashCode
class SeedReference {
    final String seedJobName
    String templateJobName
    String digest

    SeedReference(String seedJobName) {
        this(null, seedJobName, null)
    }

    SeedReference(String templateJobName, String seedJobName, String digest) {
        this.templateJobName = templateJobName
        this.seedJobName = seedJobName
        this.digest = digest
    }
}
