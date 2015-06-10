package javaposse.jobdsl.dsl.helpers.publisher;

import groovy.util.Node;
import groovy.util.NodeBuilder;
import javaposse.jobdsl.dsl.AbstractContext;
import javaposse.jobdsl.dsl.JobManagement;
import javaposse.jobdsl.dsl.RequiresPlugin;

import java.util.List;

/**
 * Created by tomcat on 6/10/15.
 */
public class TestNGDataPublishersContext extends AbstractContext {
    final List<Node> testDataPublishers = []

    TestNGDataPublishersContext(JobManagement jobManagement) {
        super(jobManagement)
    }

}
