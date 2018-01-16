ruleset {
    ruleset('rulesets/junit.xml') {
        // Spock ...
        exclude 'JUnitPublicNonTestMethod'
    }

    ruleset('file:config/codenarc/rules.groovy') {
        'UnusedVariable' {
            ignoreVariableNames = 'ignored'
        }

        // that's OK for test code
        exclude 'ComparisonWithSelf'
        // that's OK for test code
        exclude 'ExplicitCallToCompareToMethod'
        // that's OK for test code
        exclude 'ExplicitCallToEqualsMethod'
        // Spock encourages to violate this rule
        exclude 'MethodName'
        // that's OK for test code
        exclude 'MethodParameterTypeRequired'
        // Spock encourages to violate this rule
        exclude 'MethodReturnTypeRequired'
        // that's OK for test code
        exclude 'NoDef'
        // Fields annotated with @org.junit.Rule violate this rule
        exclude 'NonFinalPublicField'
        // Fields annotated with @org.junit.Rule violate this rule
        exclude 'PublicInstanceField'
        // Spock's data tables violate this rule
        exclude 'UnnecessaryBooleanExpression'
        // causes false negatives
        exclude 'UnusedObject'
        // that's OK for test code
        exclude 'VariableTypeRequired'
    }
}
