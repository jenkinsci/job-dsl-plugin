package javaposse.jobdsl.dsl.helpers.scm

enum SvnCheckoutStrategy {
    Update('hudson.scm.subversion.UpdateUpdater'),
    Checkout('hudson.scm.subversion.CheckoutUpdater'),
    UpdateWithClean('hudson.scm.subversion.UpdateWithCleanUpdater'),
    UpdateWithRevert('hudson.scm.subversion.UpdateWithRevertUpdater')

    final String className

    SvnCheckoutStrategy(String className) {
        this.className = className
    }
}
