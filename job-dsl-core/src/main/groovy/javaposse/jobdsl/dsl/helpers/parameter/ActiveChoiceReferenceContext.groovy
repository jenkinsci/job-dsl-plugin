package javaposse.jobdsl.dsl.helpers.parameter

class ActiveChoiceReferenceContext extends ActiveChoiceContext {
    Set<String> referencedParameters = []

    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }

    @Override
    Node createActiveChoiceNode(String paramName) {
        Closure additionalParams = {
            referencedParameters(referencedParameters.join(','))
        }

        createGenericActiveChoiceNode('org.biouno.unochoice.CascadeChoiceParameter', paramName, this, 'PT_',
                additionalParams)
    }
}
