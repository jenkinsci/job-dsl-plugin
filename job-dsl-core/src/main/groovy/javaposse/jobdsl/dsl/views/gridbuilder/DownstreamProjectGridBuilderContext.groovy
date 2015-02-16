package javaposse.jobdsl.dsl.views.gridbuilder

import javaposse.jobdsl.dsl.Context

class DownstreamProjectGridBuilderContext implements Context {

    String firstJob

    void firstJob(String firstJob) {
        this.firstJob = firstJob
    }
}
