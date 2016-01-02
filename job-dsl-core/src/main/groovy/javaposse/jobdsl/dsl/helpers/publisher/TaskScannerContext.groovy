package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class TaskScannerContext extends StaticAnalysisContext {
    protected final JobManagement jobManagement

    boolean regularExpression

    TaskScannerContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    /**
     * If set, treats the tag identifiers as regular expression.
     *
     * @since 1.42
     */
    @RequiresPlugin(id = 'tasks', minimumVersion = '4.41')
    void regularExpression(boolean regularExpression = true) {
        this.regularExpression = regularExpression
    }
}
