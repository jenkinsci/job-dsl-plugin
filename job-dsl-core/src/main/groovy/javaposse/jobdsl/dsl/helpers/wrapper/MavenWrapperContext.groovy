package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class MavenWrapperContext extends WrapperContext {
    MavenWrapperContext(JobManagement jobManagement, Item item) {
        super(jobManagement, item)
    }

    /**
     * Allows to perform a release build using the
     * <a href="http://maven.apache.org/maven-release/maven-release-plugin/">maven-release-plugin</a>.
     *
     * @since 1.25
     */
    @RequiresPlugin(id = 'm2release')
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
