// add group permissions to see the workspace of the job
job('example-1') {
    authorization {
        permission('hudson.model.Item.Workspace:authenticated')
        // requires matrix-auth > 3.0
        permission('GROUP:hudson.model.Item.Workspace:group1')
        groupPermission('hudson.model.Item.Workspace', 'group2')
    }
}

// adds the build permission to users
job('example-2') {
    authorization {
        permission('hudson.model.Item.Build', 'anonymous')
        // requires matrix-auth > 3.0
        permission('USER:hudson.model.Item.Build:user1')
        userPermission('hudson.model.Item.Build', 'user2')
    }
}

// add all permissions to users or groups, blocking inheritance of the global
// authorization matrix
job('example-3') {
    authorization {
        permissionAll('joe')
        blocksInheritance()
        // requires matrix-auth > 3.0
        userPermissionAll('user1')
        groupPermissionAll('group1')
    }
}

// gives the hudson.model.Item.Discover and hudson.model.Item.Create permission to user resp. groups
job('example-4') {
    authorization {
        permissions('jill', [
            'hudson.model.Item.Create',
            'hudson.model.Item.Discover'
        ])
        // requires matrix-auth > 3.0
        userPermissions('user1', [
            'hudson.model.Item.Create',
            'hudson.model.Item.Discover'
        ])
        groupPermissions('group1', [
            'hudson.model.Item.Create',
            'hudson.model.Item.Discover'
        ])
    }
}
