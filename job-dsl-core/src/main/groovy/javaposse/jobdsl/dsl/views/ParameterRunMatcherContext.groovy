package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context

class ParameterRunMatcherContext implements Context {
    String matchType = 'includeMatched'
    String nameRegex = ''
    String valueRegex = ''
    String descriptionRegex = ''
    boolean useDefaultValue = false
    boolean matchAllBuilds = true
    int maxBuildsToMatch = 0
    boolean matchBuildsInProgress = false

    /**
     * Include/exclude mode.
     * Valid values: {@code 'includeMatched'}, {@code 'includeUnmatched'},
     * {@code 'excludeMatched'}, {@code 'excludeUnmatched'}.
     */
    void matchType(String matchType) {
        this.matchType = matchType
    }

    /**
     * Regex to match parameter names.
     */
    void nameRegex(String nameRegex) {
        this.nameRegex = nameRegex
    }

    /**
     * Regex to match parameter values.
     */
    void valueRegex(String valueRegex) {
        this.valueRegex = valueRegex
    }

    /**
     * Regex to match parameter descriptions.
     */
    void descriptionRegex(String descriptionRegex) {
        this.descriptionRegex = descriptionRegex
    }

    /**
     * If {@code true}, matches against the parameter's default value instead of actual build value.
     */
    void useDefaultValue(boolean useDefaultValue = true) {
        this.useDefaultValue = useDefaultValue
    }

    /**
     * If {@code true}, scans build history (not just last build). Defaults to {@code true}.
     */
    void matchAllBuilds(boolean matchAllBuilds = true) {
        this.matchAllBuilds = matchAllBuilds
    }

    /**
     * Maximum builds to scan. 0 = unlimited.
     */
    void maxBuildsToMatch(int maxBuildsToMatch) {
        this.maxBuildsToMatch = maxBuildsToMatch
    }

    /**
     * If {@code true}, also matches currently running builds.
     */
    void matchBuildsInProgress(boolean matchBuildsInProgress = true) {
        this.matchBuildsInProgress = matchBuildsInProgress
    }
}
