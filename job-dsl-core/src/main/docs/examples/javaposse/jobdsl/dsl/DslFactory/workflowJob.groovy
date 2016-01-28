workflowJob('example') {
    definition {
        cps {
            script('readFile(\'project-a-workflow.groovy\')')
            sandbox()
        }
    }
}
