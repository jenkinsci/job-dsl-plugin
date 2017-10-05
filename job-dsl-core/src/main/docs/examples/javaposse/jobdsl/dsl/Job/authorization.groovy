// add a permission for the special authenticated group to see the workspace of the job
job('example-1') {
    authorization {
        permission('hudson.model.Item.Workspace:authenticated')
    }
}

// adds the build permission for the special anonymous user
job('example-2') {
    authorization {
        permission('hudson.model.Item.Build', 'anonymous')
    }
}

// add all permissions for user joe, blocking inheritance of the global
// authorization matrix
job('example-3') {
    authorization {
        permissionAll('joe')
        blocksInheritance()
    }
}

// gives the hudson.model.Item.Discover and hudson.model.Item.Create permission to jill
job('example-4') {
    authorization {
        permissions('jill', [
            'hudson.model.Item.Create',
            'hudson.model.Item.Discover'
        ])
    }
}
