package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

final class GitParamContext implements Context {
    String description = ''
    Type type = Type.TAG
    String branch = ''
    String tagFilter = '*'
    SortMode sortMode = SortMode.NONE
    String defaultValue = ''

    void description(String description) {
        this.description = description
    }

    void type(Type type) {
        this.type = type
    }

    void branch(String branch) {
        this.branch = branch
    }

    void tagFilter(String tagFilter) {
        this.tagFilter = tagFilter
    }

    void sortMode(SortMode sortMode) {
        this.sortMode = sortMode
    }

    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    static enum Type {
        TAG('PT_TAG'),
        BRANCH('PT_BRANCH'),
        BRANCH_OR_TAG('PT_BRANCH_TAG'),
        REVISION('PT_REVISION')

        Type(String value) {
            this.value = value
        }
        private final String value
    }

    static enum SortMode {
        NONE,
        ASCENDING_SMART,
        DESCENDING_SMART,
        ASCENDING,
        DESCENDING
    }
}
