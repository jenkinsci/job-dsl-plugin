package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class GitPathRestrictionContext extends AbstractContext {
    String includedRegions
    String excludedRegions

    GitPathRestrictionContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Each inclusion uses java regular expression pattern matching, and must be separated by a new line.
     * An empty list implies that everything is included.
     */
    void includedRegions(String includedRegions) {
        this.includedRegions = includedRegions
    }

    /**
     * Each exclusion uses java regular expression pattern matching, and must be separated by a new line.
     */
    void excludedRegions(String excludedRegions) {
        this.excludedRegions = excludedRegions
    }
}
