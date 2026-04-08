job('example') {
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
        activeChoiceParam('CHOICE-2') {
            description('Allows user choose from multiple choices with sandbox enabled')
            filterable()
            choiceType('SINGLE_SELECT')
            groovyScript {
                secureScript {
                    script('["choice1", "choice2"]')
                    sandbox(true)
                }
                secureFallbackScript {
                    script('"fallback choice"')
                    sandbox(true)
                }
            }
        }
    }
}
