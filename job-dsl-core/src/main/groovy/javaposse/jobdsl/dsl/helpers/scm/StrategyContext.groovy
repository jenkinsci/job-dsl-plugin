package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class StrategyContext extends AbstractContext {
    Node buildChooser

    StrategyContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    @RequiresPlugin(id = 'gerrit-trigger', minimumVersion = '2.0')
    void gerritTrigger() {
        buildChooser = NodeBuilder.newInstance().buildChooser(
                class: 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser'
        ) {
            separator('#')
        }
    }

    void inverse() {
        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.InverseBuildChooser')
    }

    @RequiresPlugin(id = 'git', minimumVersion = '2.3.1')
    void ancestry(int maxAge, String commit) {
        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.AncestryBuildChooser') {
            maximumAgeInDays(maxAge)
            ancestorCommitSha1(commit)
        }
    }
}
