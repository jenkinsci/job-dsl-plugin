package javaposse.jobdsl.dsl.helpers.parameter

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class ListTagsParamContext extends AbstractContext {
    String description
    String tagFilterRegex
    boolean sortNewestFirst
    boolean sortZtoA
    String maxTagsToDisplay
    String defaultValue
    String credentialsId

    ListTagsParamContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets a description for the parameter.
     */
    void description(String description) {
        this.description = description
    }

    /**
     * Specifies a regular expression which will be used to filter the tags which are actually displayed when triggering
     * a new build.
     */
    void tagFilterRegex(String tagFilterRegex) {
        this.tagFilterRegex = tagFilterRegex
    }

    /**
     * If set, sorts tags from newest to oldest. Defaults to {@code false}.
     */
    void sortNewestFirst(boolean sortNewestFirst = true) {
        this.sortNewestFirst = sortNewestFirst
    }

    /**
     * It set, displays tags in reverse order (sorted Z to A). Defaults to {@code false}.
     */
    void sortZtoA(boolean sortZtoA = true) {
        this.sortZtoA = sortZtoA
    }

    /**
     * Specifies the maximum number of tags to display in the dropdown. Any non-number value will default to all.
     */
    void maxTagsToDisplay(String maxTagsToDisplay) {
        this.maxTagsToDisplay = maxTagsToDisplay
    }

    /**
     * Specifies the maximum number of tags to display in the dropdown.
     */
    void maxTagsToDisplay(int maxTagsToDisplay) {
        this.maxTagsToDisplay = maxTagsToDisplay
    }

    /**
     * Sets the default value for the parameter.
     */
    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    /**
     * Sets credentials for authentication with the remote Subversion server.
     */
    void credentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }
}
