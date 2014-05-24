package javaposse.jobdsl.dsl.helpers.scm
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context

/**
 * DSL for the Clear Case plugin
 *
 * See http://wiki.jenkins-ci.org/display/JENKINS/ClearCase+Plugin
 */
class ClearCaseContext implements Context {
    private List<WithXmlAction> withXmlActions
    private JobManagement jobManagement

    private enum ChangeSet {
        NONE ("NONE"),
        BRANCH ("BRANCH"),
        UPDT ("UPDT")

        private changeSet

        private ChangeSet(String changeSet) {
            this.changeSet = changeSet
        }

        private String value() {
            return changeSet
        }
    }

    String changeSet = ChangeSet.BRANCH.value()
    boolean createDynView = false
    String excludedRegions = ''
    boolean extractLoadRules = false
    boolean filteringOutDestroySubBranchEvent = false
    boolean freezeCode = false
    String loadRules = ''
    String loadRulesForPolling = ''
    String mkviewOptionalParameter = ''
    Integer multiSitePollBuffer = 0
    boolean recreateView = false
    boolean removeViewOnRename = false
    boolean useDynamicView = false
    boolean useOtherLoadRulesForPolling = false
    boolean useUpdate = true
    String viewDrive = '/view'
    String viewName = 'Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}'
    String viewPath = 'view'
    String branch = ''
    String configSpec = ''
    String configSpecFileName = ''
    boolean doNotUpdateConfigSpec = false
    boolean extractConfigSpec = false
    String label = ''
    boolean refreshConfigSpec = false
    String refreshConfigSpecCommand = ''
    boolean useTimeRule = false

    void configSpec(String configSpec) {
        this.configSpec = configSpec
    }

    void loadRules(String loadRules) {
        this.loadRules = loadRules
    }

    void mkviewOptionalParameter(String mkviewOptionalParameter) {
        this.mkviewOptionalParameter = mkviewOptionalParameter
    }

    void viewName(String viewName) {
        this.viewName = viewName
    }

    void viewPath(String viewPath) {
        this.viewPath = viewPath
    }
}
