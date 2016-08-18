package javaposse.jobdsl.dsl.helpers.step.condition

@Deprecated
class FileExistsCondition {
    @Deprecated
    static enum BaseDir {
        JENKINS_HOME('JenkinsHome'),
        ARTIFACTS_DIR('ArtifactsDir'),
        WORKSPACE('Workspace')

        final String baseDirClass

        BaseDir(String baseDirType) {
            baseDirClass = "org.jenkins_ci.plugins.run_condition.common.BaseDirectory\$${baseDirType}"
        }
    }
}
