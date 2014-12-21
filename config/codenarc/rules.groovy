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
        // we don't care
        exclude 'Instanceof'
    }

    // the DRY rules do not necessarily lead to better code
    // ruleset('rulesets/dry.xml')

    // these rules cause compilation failure warnings
    // ruleset('rulesets/enhanced.xml')

    ruleset('rulesets/exceptions.xml')

    ruleset('rulesets/formatting.xml') {
        // enforce at least one space after map entry colon
        SpaceAroundMapEntryColon {
            characterAfterColonRegex = /\s/
            characterBeforeColonRegex = /./
        }

        // we don't care for now
        exclude 'ClassJavadoc'
    }

    ruleset('rulesets/generic.xml')

    ruleset('rulesets/groovyism.xml') {
        // framework methods should be allowed to call leftShift explicitly
        ExplicitCallToLeftShiftMethod {
            ignoreThisReference = true
        }

        // not necessarily an issue, problems should be detected by unit tests
        exclude 'GStringExpressionWithinString'
    }

    ruleset('rulesets/imports.xml') {
        // we order static imports after other imports because that's the default style in IDEA
        MisorderedStaticImports {
            comesBefore = false
        }
    }

    ruleset('rulesets/logging.xml')

    ruleset('rulesets/naming.xml') {
        // this is an issue, but currently the Context classes violate this by convention
        exclude 'ConfusingMethodName'
        // we don't care for now
        exclude 'FactoryMethodName'
    }

    ruleset('rulesets/security.xml') {
        // we don't care because our classes need not to satisfy the Java Beans specification
        exclude 'JavaIoPackageAccess'
    }

    ruleset('rulesets/serialization.xml') {
        // we don't care because we are not using Java serialization
        exclude 'SerializableClassMustDefineSerialVersionUID'
    }

    // we don't care for now
    // ruleset('rulesets/size.xml')

    ruleset('rulesets/unnecessary.xml') {
        // we don't care, does not necessarily lead to better code
        exclude 'UnnecessaryElseStatement'
        // we don't care for now, does not necessarily lead to better code
        exclude 'UnnecessaryObjectReferences'
    }

    ruleset('rulesets/unused.xml')
}
