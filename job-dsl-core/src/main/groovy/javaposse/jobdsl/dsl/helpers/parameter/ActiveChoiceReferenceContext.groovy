package javaposse.jobdsl.dsl.helpers.parameter

class ActiveChoiceReferenceContext extends ActiveChoiceContext {
    Set<String> referencedParameters = []

    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }
}
