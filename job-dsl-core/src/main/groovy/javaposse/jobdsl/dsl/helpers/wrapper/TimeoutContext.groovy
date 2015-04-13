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
     * @since 1.24
     */
    @RequiresPlugin(id = 'build-timeout', minimumVersion = '1.13')
    void noActivity(int seconds = 180) {
        setStrategy('NoActivity') {
            timeout(seconds * 1000)
        }
    }

    /**
     * @since 1.24
     */
    void absolute(int minutes = 3) {
        setStrategy('Absolute') {
            timeoutMinutes(minutes)
        }
    }

    /**
     * @since 1.24
     */
    void likelyStuck() {
        setStrategy('LikelyStuck')
    }

    /**
     * @since 1.24
     */
    void failBuild() {
        addOperation('Fail')
    }

    @Deprecated
    void failBuild(boolean fail) {
        jobManagement.logDeprecationWarning()

        if (fail) {
            failBuild()
        } else {
            operations.removeAll { it.name() == 'hudson.plugins.build__timeout.operations.FailOperation' }
        }
    }

    /**
     * @since 1.30
     */
    @RequiresPlugin(id = 'build-timeout', minimumVersion = '1.13')
    void abortBuild() {
        addOperation('Abort')
    }

    /**
     * @since 1.24
     */
    void writeDescription(String description) {
        addOperation('WriteDescription') {
            delegate.description(description)
        }
    }

    private void setStrategy(String type, Closure closure = null) {
        strategy = new NodeBuilder().strategy(
                class: "hudson.plugins.build_timeout.impl.${type}TimeOutStrategy",
                closure
        )
    }

    private void addOperation(String type, Closure closure = null) {
        operations << new NodeBuilder()."hudson.plugins.build__timeout.operations.${type}Operation"(closure)
    }
}
