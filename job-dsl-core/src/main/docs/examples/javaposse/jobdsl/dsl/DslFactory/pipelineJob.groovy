pipelineJob('example') {
    definition {
        cps {
            script(readFileFromWorkspace('project_a_workflow.groovy'))
            sandbox()
        }
    }
}
