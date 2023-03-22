package javaposse.jobdsl.plugin.fixtures;

import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.JobManagement;

public class InvalidConstructorCountContext implements Context {
    public InvalidConstructorCountContext() {}

    public InvalidConstructorCountContext(JobManagement jobManagement) {}
}
