job('example-1') {
    parameters {
        activeChoiceReactiveReferenceParam('CHOICE-1') {
            description('Allows user choose from multiple choices')
            omitValueField()
            choiceType('FORMATTED_HIDDEN_HTML')
            groovyScript {
                script('["choice1", "choice2"]')
                fallbackScript('"fallback choice"')
            }
            referencedParameter('BOOLEAN-PARAM-1')
            referencedParameter('BOOLEAN-PARAM-2')
        }
    }
}

job('example-2') {
    parameters {
        activeChoiceReactiveReferenceParam('CHOICE-1') {
            scriptlerScript('scriptler-script1.groovy') {
                parameter('param1', 'value1')
                parameter('param2', 'value2')
            }
            referencedParameter('BOOLEAN-PARAM-1')
        }
    }
}
