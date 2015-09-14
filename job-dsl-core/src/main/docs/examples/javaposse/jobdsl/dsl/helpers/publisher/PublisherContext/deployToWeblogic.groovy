job('example') {
    publishers {
        deployToWeblogic() {
            // these are the default values used for optional fields, when not overridden by closure
            mustExitOnFailure false
            forceStopOnFirstFailure false
            deployingOnlyWhenUpdates false

            deployedProjectsDependencies ''

            // min. one task is required
            task {
                // required
                weblogicEnvironmentTargetedName 'dev_environment'
                // required
                deploymentName 'myApplicationName'

                deploymentTargets 'AdminServer'

                // required
                builtResourceRegexToDeploy 'myApp\\.ear'
                // required
                baseResourcesGeneratedDirectory ''
                // required
                taskName 'Deploy myApp to DEV Server'

                jdkName 'JDK_7'
                jdkHome 'C:\\Program Files\\Java\\jdk1.7.0_65'

                // one of: WeblogicDeploymentStageModes
                stageMode WeblogicDeploymentStageModes.BY_DEFAULT
                commandLine '-debug -remote -verbose -name {wl.deployment_name} -targets {wl.targets} ' +
                        '-adminurl t3://{wl.host}:{wl.port} -user {wl.login} -password {wl.password} ' +
                        '-undeploy -noexit;\n' +

                        '-debug -remote -verbose -name {wl.deployment_name} -source {wl.source}' +
                        ' -targets {wl.targets} -adminurl t3://{wl.host}:{wl.port} -user {wl.login}' +
                        ' -password {wl.password} -deploy -stage -upload;'
                deploymentPlan ''
            }
        }
    }
}
