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
        JenkinsHome,
        ArtifactsDir,
        Workspace

        String getBaseDirClass() {
            "org.jenkins_ci.plugins.run_condition.common.BaseDirectory\$${name()}"
        }
    }
}
