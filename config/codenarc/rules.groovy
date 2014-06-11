ruleset {
    ruleset('rulesets/basic.xml')

    ruleset('rulesets/braces.xml')

    ruleset('rulesets/concurrency.xml')

    ruleset('rulesets/convention.xml') {
        // this rule does not necessarily lead to better code
        exclude 'IfStatementCouldBeTernary'
    }

    ruleset('rulesets/design.xml') {
        // we don't care
        exclude 'AbstractClassWithoutAbstractMethod'
        // we don't care
        exclude 'BuilderMethodWithSideEffects'
    }

    // the DRY rules do not necessarily lead to better code
    // ruleset('rulesets/dry.xml')

    ruleset('rulesets/enhanced.xml')

    ruleset('rulesets/exceptions.xml')

    ruleset('rulesets/formatting.xml') {
        // enforce at least one space after map entry colon
        SpaceAroundMapEntryColon {
            characterAfterColonRegex = /\s/
            characterBeforeColonRegex = /./
        }

        // we don't care for now
        exclude 'ClassJavadoc'
        // TODO: fix violations
        exclude 'LineLength'
        // TODO: fix violations
        exclude 'SpaceAroundOperator'
        // TODO: fix violations
        exclude 'SpaceBeforeClosingBrace'
        // TODO: fix violations
        exclude 'SpaceBeforeOpeningBrace'
    }

    ruleset('rulesets/generic.xml')

    ruleset('rulesets/groovyism.xml') {
        // framework methods should be allowed to call leftShift explicitly
        ExplicitCallToLeftShiftMethod {
            ignoreThisReference = true
        }

        // TODO: fix violations
        exclude 'ClosureAsLastMethodParameter'
        // TODO: fix violations
        exclude 'ExplicitArrayListInstantiation'
        // TODO: fix violations
        exclude 'ExplicitCallToGetAtMethod'
        // not necessarily an issue, problems should be detected by unit tests
        exclude 'GStringExpressionWithinString'
    }

    ruleset('rulesets/imports.xml') {
        // we order static imports after other imports because that's the default style in IDEA
        MisorderedStaticImports {
            comesBefore = false
        }

        // TODO: fix violations
        exclude 'UnusedImport'
    }

    ruleset('rulesets/logging.xml') {
        // TODO: fix violations
        exclude 'LoggerForDifferentClass'
        // TODO: fix violations
        exclude 'Println'
    }

    ruleset('rulesets/naming.xml') {
        // this is an issue, but currently the Context classes violate this by convention
        exclude 'ConfusingMethodName'
        // TODO: fix violations
        exclude 'FactoryMethodName'
        // TODO: fix violations
        exclude 'PropertyName'
        // TODO: fix violations
        exclude 'VariableName'
    }

    ruleset('rulesets/security.xml') {
        // we don't care because our classes need not to satisfy the Java Beans specification
        exclude 'JavaIoPackageAccess'
        // TODO: fix violations
        exclude 'NonFinalPublicField'
    }

    ruleset('rulesets/serialization.xml') {
        // we don't care because we are not using Java serialization
        exclude 'SerializableClassMustDefineSerialVersionUID'
    }

    // we don't care for now
    // ruleset('rulesets/size.xml')

    ruleset('rulesets/unnecessary.xml') {
        // TODO: fix violations
        exclude 'UnnecessaryCollectCall'
        // TODO: fix violations
        exclude 'UnnecessaryDefInFieldDeclaration'
        // TODO: fix violations
        exclude 'UnnecessaryDefInMethodDeclaration'
        // TODO: fix violations
        exclude 'UnnecessaryDotClass'
        // we don't care, does not necessarily lead to better code
        exclude 'UnnecessaryElseStatement'
        // TODO: fix violations
        exclude 'UnnecessaryGetter'
        // TODO: fix violations
        exclude 'UnnecessaryGString'
        // TODO: fix violations
        exclude 'UnnecessaryObjectReferences'
        // TODO: fix violations
        exclude 'UnnecessaryPackageReference'
        // TODO: fix violations
        exclude 'UnnecessaryParenthesesForMethodCallWithClosure'
        // TODO: fix violations
        exclude 'UnnecessaryPublicModifier'
        // TODO: fix violations
        exclude 'UnnecessaryReturnKeyword'
        // TODO: fix violations
        exclude 'UnnecessarySemicolon'
        // TODO: fix violations
        exclude 'UnnecessarySubstring'
    }

    ruleset('rulesets/unused.xml') {
        // TODO: fix violations
        exclude 'UnusedPrivateField'
        // TODO: fix violations
        exclude 'UnusedVariable'
    }
}
