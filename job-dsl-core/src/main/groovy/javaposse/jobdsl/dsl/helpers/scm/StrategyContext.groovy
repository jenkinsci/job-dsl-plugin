package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

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
    void gerritTrigger() {
        jobManagement.requireMinimumPluginVersion('gerrit-trigger', '2.0')

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
    void ancestry(int maxAge, String commit) {
        jobManagement.requireMinimumPluginVersion('git', '2.3.1')

        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.AncestryBuildChooser') {
            maximumAgeInDays(maxAge)
            ancestorCommitSha1(commit)
        }
    }
}
