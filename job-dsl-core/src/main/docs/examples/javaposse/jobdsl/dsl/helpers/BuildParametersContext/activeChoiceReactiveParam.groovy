job('example') {
    parameters {
        activeChoiceReactiveParam('CHOICE-1') {
            description('Allows user choose from multiple choices')
            filterable()
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('["choice1", "choice2"]')
                fallbackScript('"fallback choice"')
            }
            referencedParameter('BOOLEAN-PARAM-1')
            referencedParameter('BOOLEAN-PARAM-2')
        }
    }
}
