package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.Context

class TestDataPublishersContext implements Context {
    private final JobManagement jobManagement
    final List<Node> testDataPublishers = []

    TestDataPublishersContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    void allowClaimingOfFailedTests() {
        jobManagement.requireMinimumPluginVersion('claim', '2.0')
        testDataPublishers << new NodeBuilder().'hudson.plugins.claim.ClaimTestDataPublisher'()
    }

    void publishTestAttachments() {
        jobManagement.requireMinimumPluginVersion('junit-attachments', '1.0')
        testDataPublishers << new NodeBuilder().'hudson.plugins.junitattachments.AttachmentPublisher'()
    }

    void publishTestStabilityData() {
        jobManagement.requireMinimumPluginVersion('test-stability', '1.0')
        testDataPublishers << new NodeBuilder().'de.esailors.jenkins.teststability.StabilityTestDataPublisher'()
    }
}
