package javaposse.jobdsl.dsl.helpers

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.WithXmlAction

class TopLevelHelper implements Helper {

    List<WithXmlAction> withXmlActions

    TopLevelHelper(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }

    /**
     * "Restrict where this project can be run"
     * <assignedNode>FullTools&amp;&amp;RPM&amp;&amp;DC</assignedNode>
     * @param labelExpression
     * @return
     */
    boolean labelAlreadyAdded = false
    def label(String labelExpression) {
        Preconditions.checkState(!labelAlreadyAdded, "Label can only be appplied once")
        labelAlreadyAdded = true
        execute {
            it / assignedNode << "${labelExpression}"
        }
    }

    /*
    <buildWrappers>
      <hudson.plugins.build__timeout.BuildTimeoutWrapper>
        <timeoutMinutes>15</timeoutMinutes>
        <failBuild>true</failBuild>
      </hudson.plugins.build__timeout.BuildTimeoutWrapper>
    </buildWrappers>
    */

    def timeout(Integer timeoutInMinutes, Boolean shouldFailBuild = true) {
        execute {
            def pluginNode = it / buildWrappers / 'hudson.plugins.build__timeout.BuildTimeoutWrapper'
            pluginNode / timeoutMinutes(Integer.toString(timeoutInMinutes))
            pluginNode / failBuild(shoudFailBuild?'true':'false')
        }
    }
    /*
    <hudson.plugins.chucknorris.CordellWalkerRecorder>
      <factGenerator/>
    </hudson.plugins.chucknorris.CordellWalkerRecorder>
     */
    def chucknorris() {
        execute {
            def factGenerator = it / 'hudson.plugins.chucknorris.CordellWalkerRecorder' / factGenerator
        }
    }

    /*
    <disabled>true</disabled>
     */
    def disabled(boolean shouldDisable = true) {
        execute {
            it / disabled(shouldDisable?'true':'false')
        }
    }

    WithXmlAction execute(Closure rootClosure) {
        rootClosure.resolveStrategy = Closure.DELEGATE_FIRST
        def action = new WithXmlAction(rootClosure)
        withXmlActions << action
        return action
    }
}