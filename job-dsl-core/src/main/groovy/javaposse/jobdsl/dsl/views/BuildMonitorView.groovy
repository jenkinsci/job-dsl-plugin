package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc

class BuildMonitorView extends ListView {
    BuildMonitorView(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    @NoDoc
    void columns(@DslContext(ColumnsContext) Closure columnsClosure) {
        super.columns(columnsClosure)
    }
}
