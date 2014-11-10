package javaposse.jobdsl.dsl.helpers.step

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.ConfigFileType
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation

class MavenContext implements javaposse.jobdsl.dsl.helpers.common.MavenContext {
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

    @Override
    void rootPOM(String rootPOM) {
        this.rootPOM = rootPOM
    }

    @Override
    void goals(String goals) {
        this.goals << goals
    }

    @Override
    void mavenOpts(String mavenOpts) {
        this.mavenOpts << mavenOpts
    }

    @Override
    void localRepository(LocalRepositoryLocation location) {
        this.localRepositoryLocation = location
    }

    @Override
    void mavenInstallation(String name) {
        this.mavenInstallation = name
    }

    @Override
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
