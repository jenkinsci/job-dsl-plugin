package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresCore
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation

class MavenContext extends AbstractContext {
    String rootPOM
    List<String> goals = []
    List<String> mavenOpts = []
    Map<String, String> properties = [:]
    LocalRepositoryLocation localRepositoryLocation
    String mavenInstallation = '(Default)'
    Closure configureBlock
    String providedSettingsId
    String providedGlobalSettingsId
    boolean injectBuildVariables = true

    MavenContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies the path to the root POM.
     *
     * @param rootPOM path to the root POM
     */
    void rootPOM(String rootPOM) {
        this.rootPOM = rootPOM
    }

    /**
     * Specifies the goals to execute including other command line options.
     * When specified multiple times, the goals and options will be concatenated.
     *
     * @param goals the goals to execute
     */
    void goals(String goals) {
        this.goals << goals
    }

    /**
     * Specifies the JVM options needed when launching Maven as an external process.
     *
     * When specified multiple times, the options will be concatenated.
     *
     * @param mavenOpts JVM options needed when launching Maven
     */
    void mavenOpts(String mavenOpts) {
        this.mavenOpts << mavenOpts
    }

    /**
     * Set to use isolated local Maven repositories. Defaults to {@code LocalRepositoryLocation.LOCAL_TO_EXECUTOR}.
     *
     * @param location the local repository to use for isolation
     * @since 1.31
     */
    void localRepository(LocalRepositoryLocation location) {
        this.localRepositoryLocation = location
    }

    /**
     * Specifies the Maven installation for executing this step.
     *
     * @param name name of the Maven installation to use
     */
    void mavenInstallation(String name) {
        this.mavenInstallation = name
    }

    /**
     * Specifies the managed Maven settings to be used.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'config-file-provider')
    void providedSettings(String settingsIdOrName) {
        String settingsId = jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsIdOrName)

        this.providedSettingsId = settingsId ?: settingsIdOrName
    }

    /**
     * Specifies the managed global Maven settings to be used.
     *
     * @since 1.39
     */
    @RequiresPlugin(id = 'config-file-provider')
    void providedGlobalSettings(String settingsIdOrName) {
        String settingsId = jobManagement.getConfigFileId(ConfigFileType.GlobalMavenSettings, settingsIdOrName)

        this.providedGlobalSettingsId = settingsId ?: settingsIdOrName
    }

    /**
     * Allows direct manipulation of the generated XML. The {@code hudson.tasks.Maven} node is passed into the configure
     * block.
     *
     * @see <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/The-Configure-Block">The Configure Block</a>
     */
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }

    /**
     * Adds properties for the Maven build.
     *
     * @since 1.21
     */
    void properties(Map props) {
        properties.putAll(props)
    }

    /**
     * Adds a property for the Maven build.
     *
     * @since 1.21
     */
    void property(String key, String value) {
        properties[key] = value
    }

    /**
     * Skip injecting build variables as properties into the Maven process. Defaults to {@code true}.
     *
     * @since 1.54
     */
    @RequiresCore(minimumVersion = '2.12')
    void injectBuildVariables(boolean injectBuildVariables = true) {
        this.injectBuildVariables = injectBuildVariables
    }
}
