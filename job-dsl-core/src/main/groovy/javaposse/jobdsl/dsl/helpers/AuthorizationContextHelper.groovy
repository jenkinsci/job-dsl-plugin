package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

class AuthorizationContextHelper extends AbstractContextHelper<AuthorizationContext> {
    /**
     * Per-execution state, cleared each time, look for STATEFUL
     * TOOD initialize and support permission methods being called directly
     */
    AuthorizationContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    // STATEFUL
    List<String> authorization(Closure closure) {
        // Reset context
        def context = new AuthorizationContext()
        execute(closure, context)
        context.perms
    }

    AuthorizationContextHelper getAuthorization() {
        this
    }

    private addAuthorization(String perm) {
        withXmlActions << generateWithXmlAction( new AuthorizationContext([perm]))
    }

    def permission(String perm) {
        addAuthorization(perm)
        this
    }

    def permission(Permissions perm, String user) {
        addAuthorization("${perm.longForm}:${user}")
        this
    }

    def permission(String permEnumName, String user) {
        permission( Permissions.valueOf(Permissions, permEnumName), user)
        this
    }

    Closure generateWithXmlClosure(AuthorizationContext context) {
        List<String> perms = context.perms
        return { Node project ->
            def matrix = project / 'properties' / 'hudson.security.AuthorizationMatrixProperty'
            perms.each { String perm ->
                // Using matrix << permission(perm) will resolve permission locally on AuthorizationContextHelper
                matrix.appendNode('permission', perm)
            }
        }
    }
}
