// gives permission for the special authenticated group to create jobs in the folder
folder('example-1') {
    authorization {
        permission('hudson.model.Item.Create:authenticated')
    }
}

// gives discover permission for the special anonymous user
folder('example-2') {
    authorization {
        permission('hudson.model.Item.Discover', 'anonymous')
    }
}

// gives all permissions to the special anonymous user
folder('example-3') {
    authorization {
        permissionAll('anonymous')
    }
}

// gives the hudson.model.Item.Discover and hudson.model.Item.Create permission to jill
folder('example-4') {
    authorization {
        permissions('jill', [
            'hudson.model.Item.Create',
            'hudson.model.Item.Discover'
        ])
    }
}
