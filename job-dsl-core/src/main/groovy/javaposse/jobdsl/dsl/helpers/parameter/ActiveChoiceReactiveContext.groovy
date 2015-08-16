package javaposse.jobdsl.dsl.helpers.parameter

class ActiveChoiceReactiveContext extends ActiveChoiceContext {
    Set<String> referencedParameters = []

    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }
}
