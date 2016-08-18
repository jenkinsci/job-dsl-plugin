package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class CopyArtifactContext extends AbstractContext {
    final List<String> includePatterns = []
    final List<String> excludePatterns = []
    final List<String> parameterFilters = []
    String targetDirectory
    boolean flatten
    boolean optional
    boolean fingerprint = true
    final CopyArtifactSelectorContext selectorContext

    CopyArtifactContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        selectorContext = new CopyArtifactSelectorContext(jobManagement, item)
    }

    /**
     * Relative paths to artifact(s) to copy or leave blank to copy all artifacts. Can be called multiple times to add
     * more patterns.
     */
    void includePatterns(String... includePatterns) {
        this.includePatterns.addAll(includePatterns)
    }

    /**
     * Specify paths or patterns of artifacts to exclude. Can be called multiple times to add more patterns.
     */
    void excludePatterns(String... excludePatterns) {
        this.excludePatterns.addAll(excludePatterns)
    }

    /**
     * Specifies the target directory. Defaults to the workspace directory.
     */
    void targetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory
    }

    /**
     * Ignores the directory structure of the artifacts.
     */
    void flatten(boolean flatten = true) {
        this.flatten = flatten
    }

    /**
     * Allows this build to continue even if no build is found matching the build selector.
     */
    void optional(boolean optional = true) {
        this.optional = optional
    }

    /**
     * Automatically fingerprints all artifacts that are copied.
     */
    void fingerprintArtifacts(boolean fingerprint = true) {
        this.fingerprint = fingerprint
    }

    /**
     * Specify parameters to filter the job. Can be called multiple times to add more parameters.
     *
     * @since 1.46
     */
    void parameterFilters(String... parameterFilters) {
        this.parameterFilters.addAll(parameterFilters)
    }

    /**
     * Selects the build to copy artifacts from.
     */
    void buildSelector(@DslContext(CopyArtifactSelectorContext) Closure selectorClosure) {
        ContextHelper.executeInContext(selectorClosure, selectorContext)
    }
}
