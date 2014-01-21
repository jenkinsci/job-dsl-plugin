package javaposse.jobdsl.plugin;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
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
                null, emptyArchiveScript), true, IGNORE));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }
}
