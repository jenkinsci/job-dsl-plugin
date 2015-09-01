package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class TestNGDataPublishersContext extends AbstractContext {
    final List<Node> testDataPublishers = []

    TestNGDataPublishersContext(JobManagement jobManagement) {
        super(jobManagement)
    }

}
