package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class DeliveryPipelinesContext implements Context {
    Map<String, String> components = [:]
    List<String> regularExpressions = []

    void component(String name, String initialJobName) {
        checkNotNullOrEmpty(name, 'name must be specified')
        checkNotNullOrEmpty(initialJobName, 'initialJobName must be specified')

        components[name] = initialJobName
    }

    void regex(String regex) {
        checkNotNullOrEmpty(regex, 'regex must be specified')

        regularExpressions << regex
    }
}
