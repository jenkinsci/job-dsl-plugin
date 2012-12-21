package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.WithXmlAction

class AuthorizationContextHelper extends AbstractContextHelper<AuthorizationContext> {
    /**
     * Per-execution state, cleared each time, look for STATEFUL
     * TOOD initialize and support permission methods being called directly
     */
    AuthorizationContextHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    // STATEFUL
    List<String> authorization(Closure closure) {
        // Reset context
        def context = new AuthorizationContext()
        execute(closure, context)
        context.perms
    }

    // TODO: Support dotted notation. Currently because of perms being around for the closure, we can't just append to it outside of a closure
    AuthorizationContextHelper getAuthorization() {
        return this
    }

    private addAuthorization(String perm) {
        withXmlActions << generateWithXmlAction( new AuthorizationContext([perm]))
    }

    def permission(String perm) {
        // TODO Check formatting, e.g. has colon
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

    /**
     * Builds up perms in a closure. So that it be used to build a withXml block
     */
    static class AuthorizationContext implements Context {
        List<String> perms = []

        AuthorizationContext() {
        }

        AuthorizationContext(List<String> perms) {
            this.perms = perms
        }

        private addAuthorization(String perm) {
            perms << perm
        }

        def permissionAll(String user) {
            Permissions.values().each {
                permission(it, user)
            }
        }

        def permission(String perm) {
            // TODO Check formatting, e.g. has colon
            addAuthorization(perm)
        }

        def permission(Permissions perm, String user) {
            addAuthorization("${perm.longForm}:${user}")
        }

        def permission(String permEnumName, String user) {
            permission( Permissions.valueOf(Permissions, permEnumName), user)
        }
    }
}