package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractExtensibleContext
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.Preconditions
import javaposse.jobdsl.dsl.RequiresPlugin

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

@ContextType('org.jenkins_ci.plugins.run_condition.RunCondition')
class RunConditionContext extends AbstractExtensibleContext {
    private static final STATUSES = [
            'SUCCESS',
            'UNSTABLE',
            'FAILURE',
            'NOT_BUILT',
            'ABORTED',
    ]

    Node condition

    RunConditionContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    @Override
    protected void addExtensionNode(Node node) {
        condition = node
    }

    /**
     * Runs the build steps no matter what.
     */
    void alwaysRun() {
        this.condition = new Node(null, 'org.jenkins_ci.plugins.run_condition.core.AlwaysRun')
    }

    /**
     * Does not run the build steps.
     */
    void neverRun() {
        this.condition = new Node(null, 'org.jenkins_ci.plugins.run_condition.core.NeverRun')
    }

    /**
     * Expands the Token Macro and run the build step if it evaluates to true.
     */
    void booleanCondition(String token) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.BooleanCondition' {
            delegate.token(token)
        }
    }

    /**
     * Runs the build steps if the two strings are the same.
     */
    void stringsMatch(String arg1, String arg2, boolean ignoreCase) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.StringsMatchCondition' {
            delegate.arg1(arg1)
            delegate.arg2(arg2)
            delegate.ignoreCase(ignoreCase)
        }
    }

    /**
     * Runs the build steps if the current build has a specific cause.
     */
    void cause(String buildCause, boolean exclusiveCause) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.CauseCondition' {
            delegate.buildCause(buildCause)
            delegate.exclusiveCause(exclusiveCause)
        }
    }

    /**
     * Runs the build steps if the expression matches the label.
     */
    void expression(String expression, String label) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.ExpressionCondition' {
            delegate.expression(expression)
            delegate.label(label)
        }
    }

    /**
     * Only runs the build steps during a certain period of the day.
     */
    void time(int earliestHours, int earliestMinutes, int latestHours, int latestMinutes, boolean useBuildTime) {
        checkArgument((0..23).contains(earliestHours), 'earliestHours must be between 0 and 23')
        checkArgument((0..59).contains(earliestMinutes), 'earliestMinutes must be between 0 and 59')
        checkArgument((0..23).contains(latestHours), 'latestHours must be between 0 and 23')
        checkArgument((0..59).contains(latestMinutes), 'latestMinutes must be between 0 and 59')

        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.TimeCondition' {
            delegate.earliestHours(earliestHours)
            delegate.earliestMinutes(earliestMinutes)
            delegate.latestHours(latestHours)
            delegate.latestMinutes(latestMinutes)
            delegate.useBuildTime(useBuildTime)
        }
    }

    /**
     * Runs the build steps if the current build status is within the configured range.
     *
     * The values must be one of {@code 'SUCCESS'}, {@code 'UNSTABLE'}, {@code 'FAILURE'}, {@code 'NOT_BUILT'} or
     * {@code 'ABORTED'}.
     */
    void status(String worstResult, String bestResult) {
        int worstResultIndex = STATUSES.findIndexOf { it == worstResult }
        int bestResultIndex = STATUSES.findIndexOf { it == bestResult }

        checkArgument(worstResultIndex > -1, "worstResult must be one of ${STATUSES.join(',')}")
        checkArgument(bestResultIndex > -1, "bestResult must be one of ${STATUSES.join(',')}")
        checkArgument(worstResultIndex >= bestResultIndex, 'worstResult must be equal or worse than bestResult')

        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.StatusCondition' {
            delegate.worstResult {
                ordinal(worstResultIndex)
            }
            delegate.bestResult {
                ordinal(bestResultIndex)
            }
        }
    }

    /**
     * Run only on selected nodes.
     *
     * @since 1.41
     */
    @RequiresPlugin(id = 'run-condition', minimumVersion = '1.0')
    void nodes(Iterable<String> allowedNodes) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.NodeCondition' {
            delegate.allowedNodes {
                allowedNodes.each {
                    string(it)
                }
            }
        }
    }

    /**
     * Runs a shell script for checking the condition.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     *
     * @since 1.23
     */
    void shell(String command) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.contributed.ShellCondition' {
            delegate.command(command)
        }
    }

    /**
     * Runs a Windows batch script for checking the condition.
     *
     * Use {@link javaposse.jobdsl.dsl.DslFactory#readFileFromWorkspace(java.lang.String) readFileFromWorkspace} to read
     * the script from a file.
     *
     * @since 1.23
     */
    void batch(String command) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.contributed.BatchFileCondition' {
            delegate.command(command)
        }
    }

    /**
     * Runs the build steps if the file exists.
     *
     * @since 1.47
     */
    void fileExists(String file, BaseDir baseDir) {
        createFileExists(file, baseDir.baseDirClass)
    }

    /**
     * Runs the build steps if one or more files match the selectors.
     *
     * @since 1.47
     */
    void filesMatch(String includes, String excludes = '', BaseDir baseDir = BaseDir.WORKSPACE) {
        createFilesMatch(includes, excludes, baseDir.baseDirClass)
    }

    /**
     * Inverts the result of the selected condition.
     *
     * @since 1.23
     */
    void not(@DslContext(RunConditionContext) Closure conditionClosure) {
        RunConditionContext context = new RunConditionContext(jobManagement, item)
        ContextHelper.executeInContext(conditionClosure, context)

        this.condition = new Node(null, 'org.jenkins_ci.plugins.run_condition.logic.Not')
        this.condition.append(ContextHelper.toNamedNode('condition', context.condition))
    }

    /**
     * Runs the build steps if all of the contained conditions would run.
     *
     * @since 1.23
     */
    void and(@DslContext(RunConditionContext) Closure... conditionClosures) {
        createLogic('And', conditionClosures)
    }

    /**
     * Runs the build steps if any of the contained conditions would run.
     *
     * @since 1.23
     */
    void or(@DslContext(RunConditionContext) Closure... conditionClosures) {
        createLogic('Or', conditionClosures)
    }

    private void createFileExists(String file, String baseDir) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.FileExistsCondition' {
            delegate.file(file)
            delegate.baseDir(class: baseDir)
        }
    }

    private void createFilesMatch(String includes, String excludes, String baseDir) {
        this.condition = new NodeBuilder().'org.jenkins_ci.plugins.run_condition.core.FilesMatchCondition' {
            delegate.includes(includes)
            delegate.excludes(excludes)
            delegate.baseDir(class: baseDir)
        }
    }

    private createLogic(String name, Closure... conditionClosures) {
        List<Node> conditions = conditionClosures.collect {
            RunConditionContext context = new RunConditionContext(jobManagement, item)
            ContextHelper.executeInContext(it, context)
            context.condition
        }
        this.condition = new NodeBuilder()."org.jenkins_ci.plugins.run_condition.logic.${name}" {
            delegate.conditions {
                conditions.each { runCondition ->
                    Node container = 'org.jenkins__ci.plugins.run__condition.logic.ConditionContainer'()
                    container.append(ContextHelper.toNamedNode('condition', runCondition))
                }
            }
        }
    }

    Node getCondition() {
        Preconditions.checkNotNull(condition, 'No condition specified')
        condition
    }

    static enum BaseDir {
        JENKINS_HOME('JenkinsHome'),
        ARTIFACTS_DIR('ArtifactsDir'),
        WORKSPACE('Workspace')

        final String baseDirClass

        BaseDir(String baseDirType) {
            this.baseDirClass = "org.jenkins_ci.plugins.run_condition.common.BaseDirectory\$${baseDirType}"
        }
    }
}
