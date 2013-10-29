package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.slaves.DumbSlave;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;

import static hudson.model.Result.SUCCESS;
import static javaposse.jobdsl.plugin.RemovedJobAction.IGNORE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExecuteDslScriptsTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void scheduleBuildOnMasterUsingScriptText() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, SCRIPT), true, IGNORE));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }

    @Test
    public void scheduleBuildOnSlaveUsingScriptText() throws Exception {
        // setup
        jenkinsRule.createSlave("Node1", "label1", null);
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, SCRIPT), true, IGNORE));
        job.setAssignedLabel(Label.get("label1"));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }

    @Test
    public void scheduleBuildOnMasterUsingScriptLocation() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("false", "jobs.groovy", null), true, IGNORE));
        jenkinsRule.getInstance().getWorkspaceFor(job).child("jobs.groovy").write(SCRIPT, "UTF-8");

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }

    @Test
    public void scheduleBuildOnSlaveUsingScriptLocation() throws Exception {
        // setup
        DumbSlave slave = jenkinsRule.createSlave("Node1", "label1", null);
        new FilePath(new File(slave.getRemoteFS())).child("workspace/seed/jobs.groovy").write(SCRIPT, "UTF-8");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("false", "jobs.groovy", null), true, IGNORE));
        job.setAssignedLabel(Label.get("label1"));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }

    @Test
    public void scheduleBuildOnSlaveUsingGlob() throws Exception {
        // setup
        DumbSlave slave = jenkinsRule.createSlave("Node2", "label2", null);
        new FilePath(new File(slave.getRemoteFS())).child("workspace/seed/dslscripts/jobs.groovy").write(SCRIPT, "UTF-8");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("false", "**/*.groovy", null), true, IGNORE));
        job.setAssignedLabel(Label.get("label2"));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }

    @Test
    public void scheduleBuildOnSlaveUsingGroovyEngineLoading() throws Exception {
        // setup
        DumbSlave slave = jenkinsRule.createSlave("Node3", "label3", null);
        new FilePath(new File(slave.getRemoteFS())).child("workspace/groovyengine/jobs.groovy").write(UTIL_SCRIPT, "UTF-8");
        new FilePath(new File(slave.getRemoteFS())).child("workspace/groovyengine/util/Util.groovy").write(UTIL_CLASS, "UTF-8");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("groovyengine");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("false", "jobs.groovy", null), true, IGNORE));
        job.setAssignedLabel(Label.get("label3"));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }


    @Test
    public void scheduleBuildOnSlaveUsingGroovyEngineInSubdirectory() throws Exception {
        // setup
        DumbSlave slave = jenkinsRule.createSlave("Node4", "label4", null);
        new FilePath(new File(slave.getRemoteFS())).child("workspace/groovyengine/mydsl/jobs.groovy").write(UTIL_SCRIPT, "UTF-8");
        new FilePath(new File(slave.getRemoteFS())).child("workspace/groovyengine/mydsl/util/Util.groovy").write(UTIL_CLASS, "UTF-8");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("groovyengine");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("false", "mydsl/jobs.groovy", null), true, IGNORE));
        job.setAssignedLabel(Label.get("label4"));

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-job") instanceof FreeStyleProject);
    }

    @Test
    public void deleteJobInFolder() throws Exception {
        // setup
        jenkinsRule.jenkins.createProject(Folder.class, "folder");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script1 = "job { name '/folder/test-job' }";
        job.getBuildersList().add(
                new ExecuteDslScripts(
                        new ExecuteDslScripts.ScriptLocation("true", null, script1),
                        false,
                        RemovedJobAction.DELETE
                )
        );
        job.onCreatedFromScratch(); // need this to updateTransientActions

        FreeStyleBuild build1 = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, build1.getResult());
        assertTrue(jenkinsRule.jenkins.getItemByFullName("/folder/test-job") instanceof FreeStyleProject);

        // when
        String script2 = "job { name '/folder/different-job' }";
        job.getBuildersList().clear();
        job.getBuildersList().add(
                new ExecuteDslScripts(
                        new ExecuteDslScripts.ScriptLocation("true", null, script2),
                        false,
                        RemovedJobAction.DELETE
                )
        );

        FreeStyleBuild build2 = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, build2.getResult());
        assertTrue(jenkinsRule.jenkins.getItemByFullName("/folder/different-job") instanceof FreeStyleProject);
        assertNull(jenkinsRule.jenkins.getItemByFullName("/folder/test-job"));
    }

    private static final String SCRIPT = "" +
            "job {\n" +
            "  name('test-job')\n" +
            "}";

    private static final String UTIL_SCRIPT = "" +
        "import util.Util\n" +
        "def u = new Util().getName()\n" +
        "\n" +
        "job {\n" +
        "  name(u)\n" +
        "}";

    private static final String UTIL_CLASS = "" +
        "package util;\n" +
        "public class Util {\n" +
        "    String getName() { return \"test-job\" }\n" +
        "}";

}
