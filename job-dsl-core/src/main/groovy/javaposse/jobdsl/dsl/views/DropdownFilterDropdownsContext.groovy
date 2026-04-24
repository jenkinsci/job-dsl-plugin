package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class DropdownFilterDropdownsContext implements Context {
    List<Node> dropdownNodes = []

    /**
     * Adds a dropdown that extracts values from job folder paths using a regex capture group.
     * E.g., {@code jobNameRegex('Project', 'projects/([^/]+)/.*')} extracts project names.
     */
    void jobNameRegex(String label, String pattern) {
        checkNotNullOrEmpty(label, 'label must be specified')
        checkNotNullOrEmpty(pattern, 'pattern must be specified')

        dropdownNodes << new NodeBuilder().'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition' {
            delegate.label(label)
            sourceType('jobNameRegex')
            jobNamePattern(pattern)
            parameterName()
        }
    }

    /**
     * Adds a dropdown that populates values from a build parameter.
     * E.g., {@code buildParameter('Environment', 'env')} creates a dropdown from the 'env' parameter.
     */
    void buildParameter(String label, String parameterName) {
        checkNotNullOrEmpty(label, 'label must be specified')
        checkNotNullOrEmpty(parameterName, 'parameterName must be specified')

        dropdownNodes << new NodeBuilder().'io.jenkins.plugins.dynamic__view__filter.DropdownDefinition' {
            delegate.label(label)
            sourceType('buildParameter')
            jobNamePattern()
            delegate.parameterName(parameterName)
        }
    }
}
