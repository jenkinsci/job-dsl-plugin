package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation

class MavenContext implements Context {
    private final JobManagement jobManagement

    String rootPOM
    List<String> goals = []
    List<String> mavenOpts = []
    Map<String, String> properties = [:]
    LocalRepositoryLocation localRepositoryLocation
    String mavenInstallation = '(Default)'
    Closure configureBlock
    String providedSettingsId

    MavenContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
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
     * Specifies the goals to execute.
     *
     * @param goals the goals to execute
     */
    void goals(String goals) {
        this.goals << goals
    }

    /**
     * Specifies the JVM options needed when launching Maven as an external process.
     *
     * @param mavenOpts JVM options needed when launching Maven
     */
    void mavenOpts(String mavenOpts) {
        this.mavenOpts << mavenOpts
    }

    @Deprecated
    void localRepository(javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation location) {
        jobManagement.logDeprecationWarning()
        this.localRepositoryLocation = location.location
    }

    /**
     * Set to use isolated local Maven repositories.
     *
     * @param location the local repository to use for isolation
     * @since 1.31
     */
    void localRepository(LocalRepositoryLocation location) {
        this.localRepositoryLocation = location
    }

    /**
     * Specifies the Maven installation for executing this step or job.
     *
     * @param name name of the Maven installation to use
     */
    void mavenInstallation(String name) {
        this.mavenInstallation = name
    }

    /**
     * Specifies the managed Maven settings to be used.
     *
     * @param settings name of the managed Maven settings
     */
    void providedSettings(String settingsName) {
        String settingsId = jobManagement.getConfigFileId(ConfigFileType.MavenSettings, settingsName)
        Preconditions.checkNotNull settingsId, "Managed Maven settings with name '${settingsName}' not found"

        this.providedSettingsId = settingsId
    }

    void configure(Closure closure) {
        this.configureBlock = closure
    }

    void properties(Map props) {
        properties = properties + props
    }

    void property(String key, String value) {
        properties = properties + [(key): value]
    }
}
