package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

/**
 * Context to configure build timeouts.
 */
class TimeoutContext extends AbstractContext {
    Node strategy
    List<Node> operations = []

    TimeoutContext(JobManagement jobManagement) {
        super(jobManagement)

        // apply defaults
        absolute()
    }

    /**
     * Defines time to wait before killing the build as a percentage of the mean of the duration of the last
     * successful builds.
     *
     * @since 1.24
     */
    void elastic(int percentage = 150, int numberOfBuilds = 3, int minutesDefault = 60) {
        setStrategy('Elastic') {
            timeoutPercentage(percentage)
            delegate.numberOfBuilds(numberOfBuilds)
            timeoutMinutesElasticDefault(minutesDefault)
        }
    }

    /**
     * Aborts the build when the specified seconds have passed since the last log output.
     *
     * @since 1.24
     */
    @RequiresPlugin(id = 'build-timeout', minimumVersion = '1.13')
    void noActivity(int seconds = 180) {
        setStrategy('NoActivity') {
            timeout(seconds * 1000)
        }
    }

    /**
     * Aborts the build based on a fixed time-out.
     *
     * @since 1.24
     */
    void absolute(int minutes = 3) {
        setStrategy('Absolute') {
            timeoutMinutes(minutes)
        }
    }

    /**
     * Aborts the build based on a fixed time-out.
     *
     * @since 1.48
     */
    void absolute(String minutes) {
      setStrategy('Absolute') {
            timeoutMinutes(minutes)
      }
    }

    /**
     * Uses a heuristics based approach to detect builds that are suspiciously running for a long time.
     *
     * @since 1.24
     */
    void likelyStuck() {
        setStrategy('LikelyStuck')
    }

    /**
     * Marked the build as failed.
     *
     * @since 1.24
     */
    void failBuild() {
        addOperation('Fail')
    }

    /**
     * Aborts the build. This is a default operation performed if no operations are specified.
     * @since 1.30
     */
    @RequiresPlugin(id = 'build-timeout', minimumVersion = '1.13')
    void abortBuild() {
        addOperation('Abort')
    }

    /**
     * Sets the build description.
     *
     * @since 1.24
     */
    void writeDescription(String description) {
        addOperation('WriteDescription') {
            delegate.description(description)
        }
    }

    private void setStrategy(String type, Closure closure = {}) {
        strategy = new NodeBuilder().strategy(
                class: "hudson.plugins.build_timeout.impl.${type}TimeOutStrategy",
                closure
        )
    }

    private void addOperation(String type, Closure closure = {}) {
        operations << new NodeBuilder()."hudson.plugins.build__timeout.operations.${type}Operation"(closure)
    }
}
