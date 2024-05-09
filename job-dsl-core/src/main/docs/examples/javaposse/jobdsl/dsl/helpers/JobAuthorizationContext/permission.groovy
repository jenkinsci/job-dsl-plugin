// add permission to see the workspace of the job
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
        permission('USER:hudson.model.Item.Workspace:user1')
        userPermission('hudson.model.Item.Workspace', 'user2')
    }
}
