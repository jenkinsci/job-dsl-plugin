job('example') {
    publishers {
        deployToWeblogic {
            mustExitOnFailure()
            forceStopOnFirstFailure()
            task {
                weblogicEnvironmentTargetedName('dev_environment')
                deploymentName('myApplicationName')
                deploymentTargets('AdminServer')

                builtResourceRegexToDeploy('myApp\\.ear')
                taskName('Deploy myApp to DEV Server')

                jdkName('JDK_8')
                jdkHome('C:\\Program Files\\Java\\jdk1.8.0_212')

                stageMode(WeblogicDeploymentStageModes.STAGE)

                commandLine('-debug -remote -verbose -name {wl.deployment_name} -targets {wl.targets} ' +
                        '-adminurl t3://{wl.host}:{wl.port} -user {wl.login} -password {wl.password} ' +
                        '-undeploy -noexit;')
                commandLine('-debug -remote -verbose -name {wl.deployment_name} -source {wl.source} ' +
                        '-targets {wl.targets}-adminurl t3://{wl.host}:{wl.port} -user {wl.login} ' +
                        '-password {wl.password} -deploy -stage -upload;')
            }
        }
    }
}
