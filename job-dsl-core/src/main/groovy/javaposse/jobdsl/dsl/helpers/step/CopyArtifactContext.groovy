package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class CopyArtifactContext extends AbstractContext {
    final List<String> includePatterns = []
    final List<String> excludePatterns = []
    String parameters
    String targetDirectory
    boolean flatten
    boolean optional
    boolean fingerprint = true
    final CopyArtifactSelectorContext selectorContext = new CopyArtifactSelectorContext()

    CopyArtifactContext(JobManagement jobManagement) {
        super(jobManagement)
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

    void parameters(String parameters) {
        this.parameters = parameters
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
