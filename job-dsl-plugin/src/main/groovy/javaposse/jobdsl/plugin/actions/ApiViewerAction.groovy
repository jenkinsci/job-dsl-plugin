package javaposse.jobdsl.plugin.actions

import hudson.model.Action

class ApiViewerAction implements Action {
    final String iconFileName = 'symbol-directory plugin-job-dsl'
    final String displayName = 'Job DSL API Reference'
    final String urlName = '/plugin/job-dsl/api-viewer/index.html'
}
