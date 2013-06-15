package javaposse.jobdsl.dsl.helpers

enum CheckoutStrategy {
    Update('hudson.scm.subversion.UpdateUpdater'),
    Checkout('hudson.scm.subversion.CheckoutUpdater'),
    EmulateCheckout('hudson.scm.subversion.UpdateWithCleanUpdater'),
    UpdateWithRevert('hudson.scm.subversion.UpdateWithRevertUpdater')

    final String longForm

    CheckoutStrategy(String longForm) {
        this.longForm = longForm
    }
}
