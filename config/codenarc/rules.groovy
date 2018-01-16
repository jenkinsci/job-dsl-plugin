ruleset {
    ruleset('rulesets/basic.xml')

    ruleset('rulesets/braces.xml')

    ruleset('rulesets/concurrency.xml')

    ruleset('rulesets/convention.xml') {
        // we don't care
        exclude 'CouldBeSwitchStatement'
        // this rule does not necessarily lead to better code
        exclude 'IfStatementCouldBeTernary'
        // we don't care
        exclude 'TrailingComma'
    }

    ruleset('rulesets/design.xml') {
        // we don't care
        exclude 'AbstractClassWithoutAbstractMethod'
        // we don't care
        exclude 'BuilderMethodWithSideEffects'
        // we don't care
        exclude 'EmptyMethodInAbstractClass'
        // we don't care
        exclude 'Instanceof'
    }

    // the DRY rules do not necessarily lead to better code
    // ruleset('rulesets/dry.xml')

    // these rules cause compilation failure warnings
    // ruleset('rulesets/enhanced.xml')

    ruleset('rulesets/exceptions.xml')

    ruleset('rulesets/formatting.xml') {
        // empty blocks like {} are OK
        SpaceAfterOpeningBrace {
            ignoreEmptyBlock = true
        }
        // enforce at least one space after map entry colon
        SpaceAroundMapEntryColon {
            characterAfterColonRegex = /\s/
            characterBeforeColonRegex = /./
        }
        // empty blocks like {} are OK
        SpaceBeforeClosingBrace {
            ignoreEmptyBlock = true
        }

        // we don't care for now
        exclude 'ClassJavadoc'
        // causes false positives
        exclude 'Indentation'

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
        FieldName {
            // ignoring some names for compatibility
            ignoreFieldNames = new File('config/codenarc/ignored-field-names.txt').readLines().join(',')
        }
        // this is an issue, but currently the Context classes violate this by convention
        exclude 'ConfusingMethodName'
        // we don't care for now
        exclude 'FactoryMethodName'
    }

    ruleset('rulesets/security.xml') {
        // we don't care because our classes need not to satisfy the Java Beans specification
        exclude 'JavaIoPackageAccess'
        // we don't care for now
        exclude 'FileCreateTempFile'
    }

    ruleset('rulesets/serialization.xml') {
        // we don't care because we are not using Java serialization
        exclude 'SerializableClassMustDefineSerialVersionUID'
    }

    // we don't care for now
    // ruleset('rulesets/size.xml')

    ruleset('rulesets/unnecessary.xml') {
        UnnecessaryConstructor {
            // constructors with annotations are probably necessary
            ignoreAnnotations = true
        }

        // we don't care, does not necessarily lead to better code
        exclude 'UnnecessaryElseStatement'
        // we don't care for now, does not necessarily lead to better code
        exclude 'UnnecessaryObjectReferences'
        // we do "unnecessary" overrides for the @NoDoc annotation
        exclude 'UnnecessaryOverridingMethod'
    }

    ruleset('rulesets/unused.xml')
}
