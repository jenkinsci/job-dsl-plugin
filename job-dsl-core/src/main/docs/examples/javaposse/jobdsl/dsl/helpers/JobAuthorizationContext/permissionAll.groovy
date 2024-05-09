// add all permissions
job('example-1') {
    authorization {
        permissionAll('joe')
        // requires matrix-auth > 3.0
        userPermissionAll('user1')
        groupPermissionAll('group1')
    }
}
