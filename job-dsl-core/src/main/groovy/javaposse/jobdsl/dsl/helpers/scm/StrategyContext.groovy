package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class StrategyContext extends AbstractContext {
    Node buildChooser

    StrategyContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * This strategy must be selected when using the
     * <a href="https://wiki.jenkins-ci.org/display/JENKINS/Gerrit+Trigger">Gerrit Trigger Plugin</a>.
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
     * Build all branches except for those which match the branch specifiers.
     */
    void inverse() {
        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.InverseBuildChooser')
    }

    /**
     * Selects commits to be build by maximum age and ancestor commit.
     */
    void ancestry(int maxAge, String commit) {
        buildChooser = NodeBuilder.newInstance().buildChooser(class: 'hudson.plugins.git.util.AncestryBuildChooser') {
            maximumAgeInDays(maxAge)
            ancestorCommitSha1(commit)
        }
    }

    /**
     * Selects branches in priority order based on which branch exists.
     *
     * @since 1.45
     */
    @RequiresPlugin(id = 'git-chooser-alternative', minimumVersion = '1.1')
    void alternative() {
        buildChooser = NodeBuilder.newInstance().buildChooser(
            class: 'org.jenkinsci.plugins.git.chooser.alternative.AlternativeBuildChooser'
        )
    }
}
