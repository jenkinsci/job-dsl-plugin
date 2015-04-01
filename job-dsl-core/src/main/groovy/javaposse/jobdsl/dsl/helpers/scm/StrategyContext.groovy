package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class StrategyContext implements Context {
    private final JobManagement jobManagement

    Node buildChooser

    StrategyContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    /**
     * <buildChooser class="com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser">
     *     <separator>#</separator>
     * </buildChooser>
     */
    @RequiresPlugin(id = 'gerrit-trigger', minimumVersion = '2.0')
    void gerritTrigger() {
        buildChooser = NodeBuilder.newInstance().buildChooser(
                class: 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser'
        ) {
            separator('#')
        }
    }

    /**
     * <buildChooser class="hudson.plugins.git.util.InverseBuildChooser"/>
     */
    void inverse() {
        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.InverseBuildChooser')
    }

    /**
     * <buildChooser class="hudson.plugins.git.util.AncestryBuildChooser">
     *     <maximumAgeInDays>30</maximumAgeInDays>
     *     <ancestorCommitSha1>7a276ba867d84fb7823c8fbd9a491c2463de2a77</ancestorCommitSha1>
     * </buildChooser>
     */
    @RequiresPlugin(id = 'git', minimumVersion = '2.3.1')
    void ancestry(int maxAge, String commit) {
        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.AncestryBuildChooser') {
            maximumAgeInDays(maxAge)
            ancestorCommitSha1(commit)
        }
    }
}
