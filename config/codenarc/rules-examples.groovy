ruleset {
    ruleset('file:config/codenarc/rules.groovy') {
        // that's OK for examples
        exclude 'NoDef'
        // causes false positives
        exclude 'UnnecessarySetter'
        // that's OK for examples
        exclude 'VariableTypeRequired'
    }
}
