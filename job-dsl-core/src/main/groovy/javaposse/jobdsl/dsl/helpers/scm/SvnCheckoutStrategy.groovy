package javaposse.jobdsl.dsl.helpers.scm

    /**
     * The SvnCheckoutStrategy enumeration contains the available checkout strategies provided by the Subversion Jenkins
     * plugin.
     */
    enum SvnCheckoutStrategy {
        /**
         * Use 'svn update' whenever possible, making the build faster. But this causes the artifacts from the previous
         * build to remain when a new build starts.
         */
        Update('hudson.scm.subversion.UpdateUpdater'),

        /**
         * Delete everything first, then perform "svn checkout". While this takes time to execute, it ensures that the
         * workspace is in the pristine state.
         */
        Checkout('hudson.scm.subversion.CheckoutUpdater'),

        /**
         * Jenkins will first remove all the unversioned/modified files/directories, as well as files/directories
         * ignored by "svn:ignore", then execute "svn update". This emulates the fresh check out behaviour without the
         * cost of full checkout.
         */
        UpdateWithClean('hudson.scm.subversion.UpdateWithCleanUpdater'),

        /**
         * Do 'svn revert' before doing 'svn update'. This slows down builds a bit, but this prevents files from getting
         * modified by builds.
         */
        UpdateWithRevert('hudson.scm.subversion.UpdateWithRevertUpdater')

        final String className

        /**
         * Constructor.
         *
         * @param className Class name to be used in the checkout strategy XML.
         */
        SvnCheckoutStrategy(String className) {
            this.className = className
        }
    }
