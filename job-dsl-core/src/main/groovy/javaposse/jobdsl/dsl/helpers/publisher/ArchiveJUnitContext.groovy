package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class ArchiveJUnitContext implements Context {
    List<String> testDataPublishers = []
    
    void publishTestStabilityData() {
        testDataPublishers.add('de.esailors.jenkins.teststability.StabilityTestDataPublisher')
    }
}
