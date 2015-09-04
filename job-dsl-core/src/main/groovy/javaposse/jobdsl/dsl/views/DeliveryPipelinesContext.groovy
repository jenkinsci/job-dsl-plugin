package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class DeliveryPipelinesContext implements Context {
    Map<String, String> components = [:]
    List<String> regularExpressions = []

    /**
     * Add a pipeline by specifying name and start job.
     */
    void component(String name, String initialJobName) {
        checkNotNullOrEmpty(name, 'name must be specified')
        checkNotNullOrEmpty(initialJobName, 'initialJobName must be specified')

        components[name] = initialJobName
    }

    /**
     * Add a pipeline by specifying a regular expression.
     */
    void regex(String regex) {
        checkNotNullOrEmpty(regex, 'regex must be specified')

        regularExpressions << regex
    }
}
