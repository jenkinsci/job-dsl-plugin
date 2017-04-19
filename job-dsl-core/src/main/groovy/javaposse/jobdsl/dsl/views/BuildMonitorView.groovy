package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc

class BuildMonitorView extends ListView {
    BuildMonitorView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    @Override
    @NoDoc
    void columns(@DslContext(ColumnsContext) Closure columnsClosure) {
        super.columns(columnsClosure)
    }
}
