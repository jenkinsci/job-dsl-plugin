package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class TestDataPublishersContext implements Context {
    private final JobManagement jobManagement
    final List<Node> testDataPublishers = []

    TestDataPublishersContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    @RequiresPlugin(id = 'claim', minimumVersion = '2.0')
    void allowClaimingOfFailedTests() {
        testDataPublishers << new NodeBuilder().'hudson.plugins.claim.ClaimTestDataPublisher'()
    }

    @RequiresPlugin(id = 'junit-attachments', minimumVersion = '1.0')
    void publishTestAttachments() {
        testDataPublishers << new NodeBuilder().'hudson.plugins.junitattachments.AttachmentPublisher'()
    }

    @RequiresPlugin(id = 'test-stability', minimumVersion = '1.0')
    void publishTestStabilityData() {
        testDataPublishers << new NodeBuilder().'de.esailors.jenkins.teststability.StabilityTestDataPublisher'()
    }

    @RequiresPlugin(id = 'flaky-test-handler', minimumVersion = '1.0.0')
    void publishFlakyTestsReport() {
        testDataPublishers << new NodeBuilder().
                'com.google.jenkins.flakyTestHandler.plugin.JUnitFlakyTestDataPublisher'()
    }
}
