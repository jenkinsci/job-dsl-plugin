multiJob('example') {
    steps {
        phase('Second') {
            phaseJob('JobZ') {
                parameters {
                    propertiesFile('my1.properties')
                }
            }
        }
        phase('Third') {
            phaseJob('JobA')
            phaseJob('JobB')
            phaseJob('JobC')
        }
        phase('Fourth') {
            phaseJob('JobD') {
                currentJobParameters(false)
                enableGroovyScript(false)
                groovyScript('FILE', '/Users/sshelomentsev/example.groovy')
                resumeCondition('EXPRESSION')
                resumeGroovyScript('FILE', '/Users/sshelomentsev/example.groovy')
                bindVar('RESUME', 'DDD', '100')
                bindVarMap('JOB', new HashMap<String, String>() { {
                    put('EEE', '200')
                    put('FFF', '300')
                } })
                parameters {
                    booleanParam('cParam', true)
                    propertiesFile('my.properties')
                    sameNode()
                    matrixSubset('it.name=="hello"')
                    subversionRevision()
                    gitRevision()
                    predefinedProp('prop1', 'value1')
                    nodeLabel('lParam', 'my_nodes')
                }
                configure { phaseJobConfig ->
                    phaseJobConfig / enableCondition << 'true'
                    phaseJobConfig / condition << '${RUN_JOB} == "true"'
                }
            }
        }
    }
}
