package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

class MavenWrapperContext extends WrapperContext {
    MavenWrapperContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * <p>Configures a release using the m2release plugin.</p>
     * <p>By default the following values are applied. If an instance of a
     * closure is provided, the values from the closure will take effect.</p>
     * <pre>
     * {@code
     * <buildWrappers>
     *     <org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>
     *         <scmUserEnvVar></scmUserEnvVar>
     *         <scmPasswordEnvVar></scmPasswordEnvVar>
     *         <releaseEnvVar>IS_M2RELEASEBUILD</releaseEnvVar>
     *         <releaseGoals>-Dresume=false release:prepare release:perform</releaseGoals>
     *         <dryRunGoals>-Dresume=false -DdryRun=true release:prepare</dryRunGoals>
     *         <selectCustomScmCommentPrefix>false</selectCustomScmCommentPrefix>
     *         <selectAppendHudsonUsername>false</selectAppendHudsonUsername>
     *         <selectScmCredentials>false</selectScmCredentials>
     *         <numberOfReleaseBuildsToKeep>1</numberOfReleaseBuildsToKeep>
     *     </org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>
     * </buildWrappers>
     *}
     * </pre>
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
