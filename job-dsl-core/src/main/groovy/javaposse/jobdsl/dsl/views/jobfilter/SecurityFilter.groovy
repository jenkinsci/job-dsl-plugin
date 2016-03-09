package javaposse.jobdsl.dsl.views.jobfilter

class SecurityFilter extends AbstractJobFilter {
    boolean configurePermission
    boolean buildPermission
    boolean workspacePermission
    PermissionCheckType permissionCheck = PermissionCheckType.MUST_MATCH_ALL

    /**
     * Defaults to {@code false}.
     */
    void configurePermission(boolean configure = true) {
        this.configurePermission = configure
    }

    /**
     * Defaults to {@code false}.
     */
    void buildPermission(boolean build = true) {
        this.buildPermission = build
    }

    /**
     * Defaults to {@code false}.
     */
    void workspacePermission(boolean workspace = true) {
        this.workspacePermission = workspace
    }

    /**
     * Selects the permission type to be matched. Defaults to {@code PermissionCheckType.MUST_MATCH_ALL}.
     */
    void permissionCheck(PermissionCheckType permissionCheck) {
        this.permissionCheck = permissionCheck
    }
}
