package javaposse.jobdsl.plugin;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author Kanstantsin Shautsou
 */
public class JobDslBuildStepTest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    /**
     * Should fail the build when user requested strict paths
     */
    @Test
    public void stepShouldFail() throws Exception {
        final ExecuteDslScripts.ScriptLocation scriptLocation = new ExecuteDslScripts.ScriptLocation(
                "false", // using script text, null not allowed
                "jobs/not-existed.groovy",
                null // script text
        );
        final ExecuteDslScripts dslBuilder = new ExecuteDslScripts(scriptLocation,
                true,
                RemovedJobAction.IGNORE,
                RemovedViewAction.IGNORE,
                LookupStrategy.JENKINS_ROOT
        );

        final FreeStyleProject job = j.createFreeStyleProject("job-dsl");
        job.getBuildersList().add(dslBuilder);

        job.scheduleBuild2(0);
        j.waitUntilNoActivity();

        final FreeStyleBuild lastBuild = job.getBuilds().getLastBuild();
        Assert.assertSame("Build should fail when user requested exact path", Result.FAILURE, lastBuild.getResult());
    }

    /**
     * When ant glob contains * we can't know how many files exist, so not fail build
     */
    @Test
    public void testShouldNotFail() throws Exception {
        final ExecuteDslScripts.ScriptLocation scriptLocation = new ExecuteDslScripts.ScriptLocation(
                "false", // using script text, null not allowed
                "**/?/*.groovy",
                null // script text
        );

        final ExecuteDslScripts dslBuilder = new ExecuteDslScripts(scriptLocation,
                true,
                RemovedJobAction.IGNORE,
                RemovedViewAction.IGNORE,
                LookupStrategy.JENKINS_ROOT
        );

        final FreeStyleProject job = j.createFreeStyleProject("job-dsl-no-fail");
        job.getBuildersList().add(dslBuilder);

        job.scheduleBuild2(0);
        j.waitUntilNoActivity();

        final FreeStyleBuild lastBuild = job.getBuilds().getLastBuild();
        Assert.assertSame("Build should not fail when path contains ** and there is no files",
                Result.SUCCESS, lastBuild.getResult());
    }
}
