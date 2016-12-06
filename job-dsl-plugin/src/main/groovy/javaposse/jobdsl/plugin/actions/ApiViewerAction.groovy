package javaposse.jobdsl.plugin.actions

import hudson.model.Action

class ApiViewerAction implements Action {
    final String iconFileName = '/plugin/job-dsl/images/48x48/directory.png'
    final String displayName = 'Job DSL API Reference'
    final String urlName = '/plugin/job-dsl/api-viewer/index.html'
}
