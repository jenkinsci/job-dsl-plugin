// add all permissions for user joe
job('example') {
    authorization {
        permissionAll('joe')
    }
}
