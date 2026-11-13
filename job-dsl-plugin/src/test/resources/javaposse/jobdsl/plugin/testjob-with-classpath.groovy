import javaposse.jobdsl.plugin.shared.JenkinsUtils

job(JenkinsUtils.jobName()) {
    description('Created using an additional classpath')
}