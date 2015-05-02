package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement

class MavenWrapperContext extends WrapperContext {
    MavenWrapperContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Configures a release using the m2release plugin.
     * By default the following values are applied. If an instance of a
     * closure is provided, the values from the closure will take effect.
     *
     * @since 1.25
     */
    void mavenRelease(@DslContext(MavenReleaseContext) Closure releaseClosure = null) {
        MavenReleaseContext context = new MavenReleaseContext()
        ContextHelper.executeInContext(releaseClosure, context)

        wrapperNodes << new NodeBuilder().'org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper' {
            scmUserEnvVar context.scmUserEnvVar
            scmPasswordEnvVar context.scmPasswordEnvVar
            releaseEnvVar context.releaseEnvVar
            releaseGoals context.releaseGoals
            dryRunGoals context.dryRunGoals
            selectCustomScmCommentPrefix context.selectCustomScmCommentPrefix
            selectAppendHudsonUsername context.selectAppendJenkinsUsername
            selectScmCredentials context.selectScmCredentials
            numberOfReleaseBuildsToKeep context.numberOfReleaseBuildsToKeep
        }
    }
}
