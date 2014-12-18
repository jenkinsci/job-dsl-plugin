package javaposse.jobdsl.dsl.helpers.step.condition

class FileExistsCondition extends SimpleCondition {

    String file
    BaseDir baseDir

    FileExistsCondition(String file, BaseDir baseDir) {
        this.name = 'FileExists'
        this.file = file
        this.baseDir = baseDir
    }

    @Override
    void addArgs(NodeBuilder builder) {
        args = [file: file]
        super.addArgs(builder)
        builder.baseDir(class: baseDir.baseDirClass)
    }

    static enum BaseDir {
        JENKINS_HOME('JenkinsHome'),
        ARTIFACTS_DIR('ArtifactsDir'),
        WORKSPACE('Workspace')

        final String baseDirClass

        BaseDir(String baseDirType) {
            this.baseDirClass = "org.jenkins_ci.plugins.run_condition.common.BaseDirectory\$${baseDirType}"
        }
    }
}
