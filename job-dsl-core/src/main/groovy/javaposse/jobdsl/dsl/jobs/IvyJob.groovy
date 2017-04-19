package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc
import javaposse.jobdsl.dsl.helpers.IvyBuilderContext
import javaposse.jobdsl.dsl.helpers.step.StepContext

import static javaposse.jobdsl.dsl.Preconditions.checkState

class IvyJob extends Job {
    IvyJob(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    @Override
    @NoDoc
    void steps(@DslContext(StepContext) Closure closure) {
        throw new IllegalStateException('steps cannot be applied for Ivy jobs')
    }

    /**
     * Specifies the pattern to use to search for ivy module descriptor files (usually
     * named ivy.xml) in this project.
     *
     * @param ivyFilePattern pattern to use to search for ivy module descriptor files
     */
    void ivyFilePattern(String ivyFilePattern) {
        configure { Node project ->
            Node node = methodMissing('ivyFilePattern', ivyFilePattern)
            project / node
        }
    }

    /**
     * Specifies modules to be excluded from the build using Ant-include pattern syntax.
     *
     * @param ivyFileExcludesPattern modules to be excluded from the build
     */
    void ivyFileExcludesPattern(String ivyFileExcludesPattern) {
        configure { Node project ->
            Node node = methodMissing('ivyFileExcludesPattern', ivyFileExcludesPattern)
            project / node
        }
    }

    /**
     * Specifies the default Ivy branch name for this module/set of modules.
     *
     * @param ivyBranch default Ivy branch name
     */
    void ivyBranch(String ivyBranch) {
        configure { Node project ->
            Node node = methodMissing('ivyBranch', ivyBranch)
            project / node
        }
    }

    /**
     * Specifies the relative path to the module descriptor file from the root of
     * each module.
     *
     * @param relativePathToDescriptorFromModuleRoot relative path to the module descriptor file
     */
    void relativePathToDescriptorFromModuleRoot(String relativePathToDescriptorFromModuleRoot) {
        configure { Node project ->
            Node node = methodMissing('relativePathToDescriptorFromModuleRoot', relativePathToDescriptorFromModuleRoot)
            project / node
        }
    }

    /**
     * Specifies a custom Ivy settings file to be used when parsing Ivy module descriptors.
     *
     * @param ivySettingsFile relative path to Ivy settings file
     */
    void ivySettingsFile(String ivySettingsFile) {
        configure { Node project ->
            Node node = methodMissing('ivySettingsFile', ivySettingsFile)
            project / node
        }
    }

    /**
     * Specifies property files that need to be loaded before parsing the Ivy settings
     * file and Ivy module descriptors.
     *
     * @param ivySettingsPropertyFiles property files to load before parsing
     */
    void ivySettingsPropertyFiles(String ivySettingsPropertyFiles) {
        configure { Node project ->
            Node node = methodMissing('ivySettingsPropertyFiles', ivySettingsPropertyFiles)
            project / node
        }
    }

    /**
     * Specifies if each module should be built as a separate sub-project.
     *
     * @param perModuleBuild if each module should be built as a separate sub-project
     */
    void perModuleBuild(boolean perModuleBuild = true) {
        configure { Node project ->
            Node node = methodMissing('aggregatorStyleBuild', !perModuleBuild)
            project / node
        }
    }

    /**
     * Specifies if only modules with changes or those modules which failed or were
     * unstable in the previous build should be triggered.
     *
     * @param incrementalBuild if only changed modules should be triggered
     */
    void incrementalBuild(boolean incrementalBuild = true) {
        configure { Node project ->
            Node node = methodMissing('incrementalBuild', incrementalBuild)
            project / node
        }
    }

    /**
     * Sets the Ivy builder type to use for building the modules. Only one builder may be specified.
     */
    void ivyBuilder(@DslContext(IvyBuilderContext) Closure closure) {
        IvyBuilderContext context = new IvyBuilderContext(jobManagement, this)
        ContextHelper.executeInContext(closure, context)

        if (!context.ivyBuilderNodes.empty) {
            checkState(context.ivyBuilderNodes.size() == 1, 'Only one Ivy builder can be specified')

            configure { Node project ->
                Node ivyBuilderType = project / ivyBuilderType
                if (ivyBuilderType) {
                    // There can only be only one Ivy builder, so remove if there
                    project.remove(ivyBuilderType)
                }

                // Assuming append the only child
                project << context.ivyBuilderNodes[0]
            }
        }
    }
}
