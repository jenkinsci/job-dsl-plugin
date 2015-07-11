package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context

class ListTagsParamContext implements Context {

    String description = null
    String tagFilterRegex = ''
    boolean sortNewestFirst = false
    boolean sortZtoA = false
    String maxTagsToDisplay = 'all'
    String defaultValue = null
    String credentialsId = null

    void description(String description) {
        this.description = description
    }

    void tagFilterRegex(String tagFilterRegex) {
        this.tagFilterRegex = tagFilterRegex
    }

    void sortNewestFirst(Boolean sortNewestFirst) {
        this.sortNewestFirst = sortNewestFirst
    }

    void sortZtoA(Boolean sortZtoA) {
        this.sortZtoA = sortZtoA
    }

    void maxTagsToDisplay(String maxTagsToDisplay) {
        this.maxTagsToDisplay = maxTagsToDisplay
    }

    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }
}
