package javaposse.jobdsl.dsl.views.jobfilter

/**
 * Use Regular Expressions to include or exclude parameterized jobs based on their build parameters.
 */
class ParameterFilter extends AbstractJobFilter {
    String nameRegex = ''
    String valueRegex = ''
    String descriptionRegex = ''
    boolean useDefaultValue
    boolean matchBuildsInProgress
    boolean matchAllBuilds
    Integer maxBuildsToMatch

    /**
     * Select the regex parameter name.
     */
    void nameRegex(String nameRegex) {
        this.nameRegex = nameRegex
    }

    /**
     * Select the regex parameter value.
     */
    void valueRegex(String valueRegex) {
        this.valueRegex = valueRegex
    }

    /**
     * Select the regex parameter description.
     */
    void descriptionRegex(String descriptionRegex) {
        this.descriptionRegex = descriptionRegex
    }

    /**
     * Match the configured default values instead of the actual value from last successful build.
     * Defaults to {@code false}.
     */
    void useDefaultValue(boolean useDefaultValue = true) {
        this.useDefaultValue = useDefaultValue
    }

    /**
     * Match against builds in progress. Defaults to {@code false}.
     */
    void matchBuildsInProgress(boolean matchBuildsInProgress = true) {
        this.matchBuildsInProgress = matchBuildsInProgress
    }

    /**
     * Match against this many previous builds. Defaults to {@code false}.
     */
    void matchAllBuilds(boolean matchAllBuilds = true) {
        this.matchAllBuilds = matchAllBuilds
    }

    /**
     * Select number of builds to match. Defaults to {@code null}.
     */
    void maxBuildsToMatch(Integer maxBuildsToMatch) {
        this.maxBuildsToMatch = maxBuildsToMatch
    }
}
