ruleset {
    ruleset('file:config/codenarc/rules.groovy') {
        // that's OK for examples
        exclude 'NoDef'
    }
}
