job('example') {
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
