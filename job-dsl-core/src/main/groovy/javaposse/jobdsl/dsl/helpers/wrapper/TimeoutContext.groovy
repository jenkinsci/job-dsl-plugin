package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement

/**
 * Context to configure build timeouts.
 */
class TimeoutContext implements Context {
    private final JobManagement jobManagement
    Node strategy
    List<Node> operations = []

    TimeoutContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement

        // apply defaults
        absolute()
    }

    void elastic(int percentage = 150, int numberOfBuilds = 3, int minutesDefault = 60) {
        setStrategy('Elastic') {
            timeoutPercentage(percentage)
            delegate.numberOfBuilds(numberOfBuilds)
            timeoutMinutesElasticDefault(minutesDefault)
        }
    }

    void noActivity(int seconds = 180) {
        jobManagement.requireMinimumPluginVersion('build-timeout', '1.13')

        setStrategy('NoActivity') {
            timeout(seconds * 1000)
        }
    }

    void absolute(int minutes = 3) {
        setStrategy('Absolute') {
            timeoutMinutes(minutes)
        }
    }

    void likelyStuck() {
        setStrategy('LikelyStuck')
    }

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

    void abortBuild() {
        jobManagement.requireMinimumPluginVersion('build-timeout', '1.13')

        addOperation('Abort')
    }

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
