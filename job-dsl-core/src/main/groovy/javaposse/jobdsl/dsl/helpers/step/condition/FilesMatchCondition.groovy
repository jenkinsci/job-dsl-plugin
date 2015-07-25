package javaposse.jobdsl.dsl.helpers.step.condition

import javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir

class FilesMatchCondition extends SimpleCondition {
    String includes, excludes
    BaseDir baseDir

    FilesMatchCondition(String includes, String excludes, BaseDir baseDir) {
        this.name = 'FilesMatch'
        this.includes = includes
        this.excludes = excludes
        this.baseDir = baseDir
    }

    @Override
    void addArgs(NodeBuilder builder) {
        args = [includes: includes, excludes: excludes]
        super.addArgs(builder)
        builder.baseDir(class: baseDir.baseDirClass)
    }
}
