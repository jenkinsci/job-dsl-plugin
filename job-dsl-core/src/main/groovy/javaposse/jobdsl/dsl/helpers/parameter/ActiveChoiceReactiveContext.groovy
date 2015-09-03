package javaposse.jobdsl.dsl.helpers.parameter

class ActiveChoiceReactiveContext extends ActiveChoiceContext {
    Set<String> referencedParameters = []

    /**
     * Specifies a list of job parameters that trigger an auto-refresh.
     */
    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }
}
