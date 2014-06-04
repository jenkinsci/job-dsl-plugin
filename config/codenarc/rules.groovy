ruleset {
    ruleset('rulesets/basic.xml') {
        // TODO: fix violations
        exclude 'EqualsAndHashCode'
        // TODO: fix violations
        exclude 'EqualsOverloaded'
    }

    ruleset('rulesets/braces.xml') {
        // TODO: fix violations
        exclude 'ForStatementBraces'
        // TODO: fix violations
        exclude 'IfStatementBraces'
    }

    ruleset('rulesets/concurrency.xml')

    ruleset('rulesets/convention.xml') {
        // TODO: fix violations
        exclude 'CouldBeElvis'
        // this rule does not necessarily lead to better code
        exclude 'IfStatementCouldBeTernary'
        // TODO: fix violations
        exclude 'ParameterReassignment'
    }

    ruleset('rulesets/design.xml') {
        // TODO: fix violations
        exclude 'AbstractClassWithPublicConstructor'
        // we don't care
        exclude 'AbstractClassWithoutAbstractMethod'
        // we don't care
        exclude 'BuilderMethodWithSideEffects'
        // TODO: fix violations
        exclude 'DuplicateStringLiteral'
        // TODO: fix violations
        exclude 'PrivateFieldCouldBeFinal'
        // TODO: fix violations
        exclude 'PublicInstanceField'
    }

    // the DRY rules do not necessarily lead to better code
    // ruleset('rulesets/dry.xml')

    ruleset('rulesets/enhanced.xml')

    ruleset('rulesets/exceptions.xml') {
        // TODO: fix violations
        exclude 'ThrowRuntimeException'
    }

    ruleset('rulesets/formatting.xml') {
        // enforce at least one space after map entry colon
        SpaceAroundMapEntryColon {
            characterAfterColonRegex = /\s/
        }

        // TODO: fix violations
        exclude 'ClassJavadoc'
        // TODO: fix violations
        exclude 'ConsecutiveBlankLines'
        // TODO: fix violations
        exclude 'FileEndsWithoutNewline'
        // TODO: fix violations
        exclude 'LineLength'
        // TODO: fix violations
        exclude 'MissingBlankLineAfterImports'
        // TODO: fix violations
        exclude 'MissingBlankLineAfterPackage'
        // TODO: fix violations
        exclude 'SpaceAfterClosingBrace'
        // TODO: fix violations
        exclude 'SpaceAfterComma'
        // TODO: fix violations
        exclude 'SpaceAfterIf'
        // TODO: fix violations
        exclude 'SpaceAfterOpeningBrace'
        // TODO: fix violations
        exclude 'SpaceAfterSwitch'
        // TODO: fix violations
        exclude 'SpaceAroundMapEntryColon'
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

        // creates false negatives
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
        // we don't care, does not necessarily lead to better code
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
