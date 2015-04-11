package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class CopyArtifactContext implements Context {
    private final JobManagement jobManagement
    final List<String> includePatterns = []
    final List<String> excludePatterns = []
    String targetDirectory
    boolean flatten
    boolean optional
    boolean fingerprint = true
    final CopyArtifactSelectorContext selectorContext = new CopyArtifactSelectorContext()

    CopyArtifactContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void includePatterns(String... includePatterns) {
        this.includePatterns.addAll(includePatterns)
    }

    @RequiresPlugin(id = 'copyartifact', minimumVersion = '1.31')
    void excludePatterns(String... excludePatterns) {
        this.excludePatterns.addAll(excludePatterns)
    }

    void targetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory
    }

    void flatten(boolean flatten = true) {
        this.flatten = flatten
    }

    void optional(boolean optional = true) {
        this.optional = optional
    }

    @RequiresPlugin(id = 'copyartifact', minimumVersion = '1.29')
    void fingerprintArtifacts(boolean fingerprint = true) {
        this.fingerprint = fingerprint
    }

    void buildSelector(@javaposse.jobdsl.dsl.DslContext(CopyArtifactSelectorContext) Closure selectorClosure) {
        ContextHelper.executeInContext(selectorClosure, selectorContext)
    }
}
