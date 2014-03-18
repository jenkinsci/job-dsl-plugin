package javaposse.jobdsl.plugin;

import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static hudson.model.Result.SUCCESS;
import static javaposse.jobdsl.plugin.RemovedJobAction.IGNORE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextIntegrationTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testMavenPrePostBuildSteps() throws Exception {
        String mavenPrePostScript = "" +
                "job(type: 'Maven') {\n" +
                "  name('maven-job')\n" +
                "  goals('clean install')\n" +
                "  preBuildSteps {\n" +
                "    shell('echo first')\n" +
                "  }\n" +
                "  postBuildSteps {\n" +
                "    shell('echo second')\n" +
                "  }\n" +
                "}";

        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true",
                null, mavenPrePostScript), true, IGNORE, RelativeNameContext.SEED_JOB));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("maven-job") instanceof MavenModuleSet);
        MavenModuleSet mavenJob = (MavenModuleSet)jenkinsRule.getInstance().getItem("maven-job");
        assertTrue(mavenJob.getPrebuilders().size() == 1);
        assertTrue(mavenJob.getPostbuilders().size() == 1);
        assertTrue(mavenJob.getPrebuilders().get(0) instanceof Shell);
        assertTrue(mavenJob.getPostbuilders().get(0) instanceof Shell);
    }

    @Test
    public void testAllowEmptyArchive() throws Exception {
        String emptyArchiveScript = "" +
                "job {\n" +
                "  name('test-job')\n" +
                "  steps {\n" +
                "    shell(\"echo 'foo' > test\")\n" +
                "  }\n" +
                "  publishers {\n" +
                "    archiveArtifacts('test*')\n" +
                "  }\n" +
                "}";

        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true",
                null, emptyArchiveScript), true, IGNORE, RelativeNameContext.SEED_JOB));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }
}
