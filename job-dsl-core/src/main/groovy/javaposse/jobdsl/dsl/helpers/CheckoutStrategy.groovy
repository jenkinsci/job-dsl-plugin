package javaposse.jobdsl.dsl.helpers

enum CheckoutStrategy {
    Update('hudson.scm.subversion.UpdateUpdater'),
    Checkout('hudson.scm.subversion.CheckoutUpdater'),
    UpdateWithClean('hudson.scm.subversion.UpdateWithCleanUpdater'),
    UpdateWithRevert('hudson.scm.subversion.UpdateWithRevertUpdater')

    final String className

    CheckoutStrategy(String className) {
        this.className = className
    }
}
