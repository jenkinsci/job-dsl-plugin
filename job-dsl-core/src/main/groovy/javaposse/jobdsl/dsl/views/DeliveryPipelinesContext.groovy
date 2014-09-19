package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.helpers.Context

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Strings.isNullOrEmpty

class DeliveryPipelinesContext implements Context {
    Map<String, String> components = [:]
    List<String> regularExpressions = []

    void component(String name, String initialJobName) {
        checkArgument(!isNullOrEmpty(name), 'name must be specified')
        checkArgument(!isNullOrEmpty(initialJobName), 'initialJobName must be specified')

        components[name] = initialJobName
    }

    void regex(String regex) {
        checkArgument(!isNullOrEmpty(regex), 'regex must be specified')

        regularExpressions << regex
    }
}
