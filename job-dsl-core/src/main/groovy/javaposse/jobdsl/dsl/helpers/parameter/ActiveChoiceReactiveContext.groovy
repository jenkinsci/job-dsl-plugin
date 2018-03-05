package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.JobManagement

class ActiveChoiceReactiveContext extends ActiveChoiceContext {
    Set<String> referencedParameters = []

    ActiveChoiceReactiveContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies a list of job parameters that trigger an auto-refresh.
     */
    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }
}
