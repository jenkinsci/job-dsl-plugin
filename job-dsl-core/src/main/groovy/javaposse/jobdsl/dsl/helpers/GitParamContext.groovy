package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

import static com.google.common.base.Preconditions.checkArgument

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

    void description(String description) {
        this.description = description
    }

    void type(String type) {
        checkArgument(VALID_TYPES.contains(type), "type must be one of ${VALID_TYPES.join(', ')}")
        this.type = type
    }

    void branch(String branch) {
        this.branch = branch
    }

    void tagFilter(String tagFilter) {
        this.tagFilter = tagFilter
    }

    void sortMode(String sortMode) {
        checkArgument(VALID_SORT_MODES.contains(sortMode), "sortMode must be one of ${VALID_SORT_MODES.join(', ')}")
        this.sortMode = sortMode
    }

    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }
}
