pipelineJob('example') {
    definition {
        cps {
            script(readFileFromWorkspace('project-a-workflow.groovy'))
        }
    }
}
