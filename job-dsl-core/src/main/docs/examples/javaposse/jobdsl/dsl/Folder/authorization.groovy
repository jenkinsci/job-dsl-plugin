// gives permission to create jobs in the folder
folder('example-1') {
    authorization {
        permission('hudson.model.Item.Create:authenticated')
        // requires matrix-auth > 3.0
        permission('GROUP:hudson.model.Item.Create:group1')
        permission('USER:hudson.model.Item.Create:user1')
        groupPermission('hudson.model.Item.Create', 'group2')
        userPermission('hudson.model.Item.Create', 'user2')
    }
}

// gives discover permission
folder('example-2') {
    authorization {
        permission('hudson.model.Item.Discover', 'anonymous')
        // requires matrix-auth > 3.0
        permission('USER:hudson.model.Item.Discover:anonymous')
        userPermission('hudson.model.Item.Discover', 'anonymous')
    }
}

// gives all permissions
folder('example-3') {
    authorization {
        permissionAll('anonymous')
        // requires matrix-auth > 3.0
        userPermissionAll('user1')
        groupPermissionAll('group1')
    }
}

// gives the hudson.model.Item.Discover and hudson.model.Item.Create permissions
folder('example-4') {
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
