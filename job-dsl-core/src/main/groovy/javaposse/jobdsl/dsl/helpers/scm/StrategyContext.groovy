package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.Context

class StrategyContext implements Context {
    private final List<WithXmlAction> withXmlActions

    List<Node> settings = []

    StrategyContext(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }
/*
    <extensions>
      <hudson.plugins.git.extensions.impl.BuildChooserSetting>
        <buildChooser
            class="com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser"
            plugin="gerrit-trigger@2.12.0">
          <separator>#</separator>
        </buildChooser>
      </hudson.plugins.git.extensions.impl.BuildChooserSetting>
      <hudson.plugins.git.extensions.impl.BuildChooserSetting>
        <buildChooser class="hudson.plugins.git.util.InverseBuildChooser"/>
      </hudson.plugins.git.extensions.impl.BuildChooserSetting>
    </extensions>

 */
    void gerritTrigger() {
        settings << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.BuildChooserSetting' {
            buildChooser(
                class: 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser',
                plugin: 'gerrit-trigger@2.12.0') {
                separator('#')
            }
        }
    }

    void inverse() {
        settings << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.BuildChooserSetting' {
            buildChooser(class: 'hudson.plugins.git.util.InverseBuildChooser')
        }
    }

    void ancestry(int maxAge /* days */, String commit) {
        settings << NodeBuilder.newInstance().'hudson.plugins.git.extensions.impl.BuildChooserSetting' {
            buildChooser(class: 'hudson.plugins.git.util.AncestryBuildChooser') {
                maximumAgeInDays(maxAge)
                ancestorCommitSha1(commit)
            }
        }
    }
}
