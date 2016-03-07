package javaposse.jobdsl.dsl.views.jobfilter

enum ScmType {

    NULL('hudson.scm.NullSCM'),
    CLEAR_CASE_BASE('hudson.plugins.clearcase.ClearCaseSCM'),
    CLEAR_CASE_UCM('hudson.plugins.clearcase.ClearCaseUcmSCM'),
    CVS_PROJECTSET('hudson.scm.CvsProjectset'),
    CVS('hudson.scm.CVSSCM'),
    GIT('hudson.plugins.git.GitSCM'),
    MERCURIAL('hudson.plugins.mercurial.MercurialSCM'),
    PERFORCE('org.jenkinsci.plugins.p4.PerforceScm'),
    SVN('hudson.scm.SubversionSCM')

    final String value

    ScmType(String value) {
        this.value = value
    }
}
