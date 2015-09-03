job('example-1') {
    parameters {
        activeChoiceParam('CHOICE-1') {
            description('Allows user choose from multiple choices')
            filterable()
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('["choice1", "choice2"]')
                fallbackScript('"fallback choice"')
            }
        }
    }
}

job('example-2') {
    parameters {
        activeChoiceParam('CHOICE-1') {
            description('Allows user choose from multiple choices')
            filterable()
            choiceType('SINGLE_SELECT')
            scriptlerScript('scriptler-script1.groovy') {
                parameter('param1', 'value1')
                parameter('param2', 'value2')
            }
        }
    }
}
