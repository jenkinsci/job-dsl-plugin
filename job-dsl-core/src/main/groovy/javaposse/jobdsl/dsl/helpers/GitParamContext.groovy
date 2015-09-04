package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

final class GitParamContext implements Context {
    private static final Set<String> VALID_TYPES = [
            'TAG', 'BRANCH', 'BRANCH_TAG', 'REVISION'
    ]
    private static final Set<String> VALID_SORT_MODES = [
            'NONE', 'ASCENDING_SMART', 'DESCENDING_SMART', 'ASCENDING', 'DESCENDING'
    ]

    String description
    String type = 'TAG'
    String branch
    String tagFilter
    String sortMode = 'NONE'
    String defaultValue

    /**
     * Sets a description for the parameter.
     */
    void description(String description) {
        this.description = description
    }

    /**
     * Specifies the type of selectable values.
     *
     * Must be one of {@code 'TAG'} (default), {@code 'BRANCH'}, {@code 'BRANCH_TAG'} or {@code 'REVISION'}.
     */
    void type(String type) {
        checkArgument(VALID_TYPES.contains(type), "type must be one of ${VALID_TYPES.join(', ')}")
        this.type = type
    }

    /**
     * Set the name of branch to look in.
     */
    void branch(String branch) {
        this.branch = branch
    }

    /**
     * Specifies a filter for tags.
     */
    void tagFilter(String tagFilter) {
        this.tagFilter = tagFilter
    }

    /**
     * Specifies the sort order for tags.
     *
     * Must be one of {@code 'NONE'} (default), {@code 'ASCENDING_SMART'}, {@code 'DESCENDING_SMART'},
     * {@code 'ASCENDING'} or {@code 'DESCENDING'}.
     */
    void sortMode(String sortMode) {
        checkArgument(VALID_SORT_MODES.contains(sortMode), "sortMode must be one of ${VALID_SORT_MODES.join(', ')}")
        this.sortMode = sortMode
    }

    /**
     * Sets a default value for the parameter.
     */
    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }
}
