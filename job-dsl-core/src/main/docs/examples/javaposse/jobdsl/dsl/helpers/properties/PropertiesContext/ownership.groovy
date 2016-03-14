job('example') {
    properties {
        ownership {
            primaryOwnerId('User_ID')
            coOwnerIds('User1', 'User2')
            coOwnerIds('User3')
            coOwnerIds('User4')
        }
    }
}
