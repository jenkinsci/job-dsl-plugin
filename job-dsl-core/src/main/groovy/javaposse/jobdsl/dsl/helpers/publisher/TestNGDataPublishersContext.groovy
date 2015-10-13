package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class TestNGDataPublishersContext extends AbstractContext {
    final List<Node> testDataPublishers = []

    TestNGDataPublishersContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Allows claiming of failed tests.
     */
    @RequiresPlugin(id = 'claim', minimumVersion = '2.0')
    void allowClaimingOfFailedTests() {
        testDataPublishers << new NodeBuilder().'hudson.plugins.claim.ClaimTestDataPublisher'()
    }

    /**
     * Publishes the test stability history.
     */
    @RequiresPlugin(id = 'test-stability', minimumVersion = '1.0')
    void publishTestStabilityData() {
        testDataPublishers << new NodeBuilder().'de.esailors.jenkins.teststability.StabilityTestDataPublisher'()
    }
}
