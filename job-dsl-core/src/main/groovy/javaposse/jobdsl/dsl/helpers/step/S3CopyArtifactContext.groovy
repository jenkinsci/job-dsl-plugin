package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

/**
 * Root node for the S3CopyArtifacts build step. Almost identical to
 * {@link javaposse.jobdsl.dsl.helpers.step.CopyArtifactContext}.
 */
@SuppressWarnings('ConfusingMethodName')
class S3CopyArtifactContext extends AbstractContext {
    final List<String> includePatterns = []
    final List<String> excludePatterns = []
    String targetDirectory
    boolean flatten
    boolean optional
    final CopyArtifactSelectorContext selectorContext

    S3CopyArtifactContext(JobManagement jobManagement, Item item) {
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
     * Selects the build to copy artifacts from.
     */
    void buildSelector(@DslContext(javaposse.jobdsl.dsl.helpers.step.CopyArtifactSelectorContext)
                               Closure selectorClosure) {
        ContextHelper.executeInContext(selectorClosure, selectorContext)
    }
}
