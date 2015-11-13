job('example') {
    properties {
        jobOwnership {
            primaryOwnerId('User_ID')
            coOwnerIds('User1', 'User2')
            coOwnerIds('User3')
            coOwnerIds('User4')
        }
    }
}
