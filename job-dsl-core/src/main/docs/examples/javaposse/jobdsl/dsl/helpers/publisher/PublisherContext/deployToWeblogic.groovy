job('example') {
    publishers {
        deployToWeblogic {
            mustExitOnFailure(true)
            forceStopOnFirstFailure(true)

            // at least one task is required
            task {
                // required
                weblogicEnvironmentTargetedName('dev_environment')
                // required
                deploymentName('myApplicationName')

                deploymentTargets('AdminServer')

                // required
                builtResourceRegexToDeploy('myApp\\.ear')
                // required
                baseResourcesGeneratedDirectory('')
                // required
                taskName('Deploy myApp to DEV Server')

                jdkName('JDK_7')
                jdkHome('C:\\Program Files\\Java\\jdk1.7.0_65')

                stageMode(WeblogicDeploymentStageModes.STAGE)

                commandLine('-debug -remote -verbose')
                commandLine('-name {wl.deployment_name} -targets {wl.targets}')
                commandLine('-adminurl t3://{wl.host}:{wl.port} -user {wl.login} -password {wl.password}')
                commandLine('-undeploy -noexit;\n')

                commandLine('-debug -remote -verbose')
                commandLine('-name {wl.deployment_name} -source {wl.source} -targets {wl.targets}')
                commandLine('-adminurl t3://{wl.host}:{wl.port} -user {wl.login} -password {wl.password}')
                commandLine('-deploy -stage -upload;')
            }
        }
    }
}
