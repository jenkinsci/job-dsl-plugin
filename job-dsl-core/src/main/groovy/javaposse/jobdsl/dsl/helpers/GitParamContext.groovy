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
    String branchFilter
    String sortMode = 'NONE'
    String defaultValue
    Boolean quickFilterEnabled
    String useRepository

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
     * Regex used to filter displayed branches. If blank, the filter will default to ".*".
     * Remote branches will be listed with the remote name first. E.g., "origin/master"
     */
    void branchFilter(String branchFilter) {
        this.branchFilter = branchFilter
    }
    /**
     * When this option is enabled will show a text field.
     * Parameter is filtered on the fly.
     */
    void quickFilterEnabled(boolean quickFilterEnabled) {
        this.quickFilterEnabled = quickFilterEnabled
    }
    /**
     *If in the task is defined multiple repositories parameter specifies which the repository is taken into account.
     * If the parameter is not defined, is taken first defined repository.
     * The parameter is a regular expression which is compared with a URL repository.
     */
    void useRepository(String useRepository) {
        this.useRepository = useRepository
    }

    /**
     * Sets a default value for the parameter.
     */
    void defaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }
}
