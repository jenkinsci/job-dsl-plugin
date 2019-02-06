package javaposse.jobdsl.dsl.helpers.toplevel

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin
import javaposse.jobdsl.dsl.jobs.MatrixJob

import static javaposse.jobdsl.dsl.Preconditions.checkState

class ThrottleConcurrentBuildsContext extends AbstractContext {
    private final Item item

    boolean throttleDisabled
    List<String> categories = []
    int maxConcurrentPerNode
    int maxConcurrentTotal
    boolean throttleMatrixBuilds = true
    boolean throttleMatrixConfigurations = true
    boolean limitOneJobWithMatchingParams = false
    String paramsToUseForLimit = ''

    ThrottleConcurrentBuildsContext(JobManagement jobManagement, Item item) {
        super(jobManagement)
        this.item = item
    }

    /**
     * Disables the throttle. Defaults to {@code false}.
     */
    void throttleDisabled(boolean throttleDisabled = true) {
        this.throttleDisabled = throttleDisabled
    }

    /**
     * Throttles this project as part of one or more categories.
     */
    void categories(List<String> categories) {
        this.categories = categories
    }

    /**
     * Sets the maximum number of concurrent builds of this project (or category) to be allowed to run per node.
     */
    void maxPerNode(int maxPerNode) {
        this.maxConcurrentPerNode = maxPerNode
    }

    /**
     * Sets the maximum number of concurrent builds of this project (or category) to be allowed to run at any one time,
     * across all nodes.
     */
    void maxTotal(int maxTotal) {
        this.maxConcurrentTotal = maxTotal
    }

    /**
     * If set, throttles Matrix master builds. Defaults to {@code true}.
     *
     * @since 1.48
     */
    @RequiresPlugin(id = 'throttle-concurrents', minimumVersion = '1.8.3')
    void throttleMatrixBuilds(boolean throttleMatrixBuilds = true) {
        checkState(item instanceof MatrixJob, 'throttleMatrixBuilds can only be used in matrix jobs')

        this.throttleMatrixBuilds = throttleMatrixBuilds
    }

    /**
     * If set, throttles Matrix configuration builds. Defaults to {@code true}.
     *
     * @since 1.48
     */
    @RequiresPlugin(id = 'throttle-concurrents', minimumVersion = '1.8.3')
    void throttleMatrixConfigurations(boolean throttleMatrixConfigurations = true) {
        checkState(item instanceof MatrixJob, 'throttleMatrixConfigurations can only be used in matrix jobs')

        this.throttleMatrixConfigurations = throttleMatrixConfigurations
    }

    /**
     * If this box is checked, only one instance of the job with matching parameter values will be allowed
     * to run at a given time. Other instances of this job with different parameter values will be allowed
     * to run concurrently.
     *
     * Optionally, provide a comma-separated list of parameters to use when comparing jobs. If blank,
     * all parameters must match for a job to be limited to one running instance.
     *
     * Defaults to {@code false}.
     *
     * @since 1.72
     */
    @RequiresPlugin(id = 'throttle-concurrents', minimumVersion = '1.8.5')
    void limitOneJobWithMatchingParams(boolean limitOneJobWithMatchingParams = false) {
        this.limitOneJobWithMatchingParams = limitOneJobWithMatchingParams
    }

    /**
     * List of parameters to check (using whitespace as the separator).
     *
     * Used if limitOneJobWithMatchingParams set to true.
     *
     * Defaults to {@code ''}.
     *
     * @since 1.72
     */
    @RequiresPlugin(id = 'throttle-concurrents', minimumVersion = '1.8.5')
    void paramsToUseForLimit(String paramsToUseForLimit = '') {
        this.paramsToUseForLimit = paramsToUseForLimit
    }

}
