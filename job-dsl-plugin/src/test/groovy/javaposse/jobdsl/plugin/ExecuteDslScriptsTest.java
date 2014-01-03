package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.AbstractItem;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.slaves.DumbSlave;
import hudson.util.StreamTaskListener;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

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
    public void deleteJob() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script1 = "job { name 'test-job' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        runBuild(job, builder1);

        // then
        assertTrue(jenkinsRule.jenkins.getItemByFullName("test-job") instanceof FreeStyleProject);

        // when
        String script2 = "job { name 'different-job' }";
        ExecuteDslScripts builder2 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script2), false, RemovedJobAction.DELETE);
        runBuild(job, builder2);

        // then
        assertTrue(jenkinsRule.jenkins.getItemByFullName("different-job") instanceof FreeStyleProject);
        assertNull(jenkinsRule.jenkins.getItemByFullName("test-job"));
    }

    @Test
    public void deleteJobInFolder() throws Exception {
        // setup
        jenkinsRule.jenkins.createProject(Folder.class, "folder");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script1 = "job { name '/folder/test-job' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        runBuild(job, builder1);

        // then
        assertTrue(jenkinsRule.jenkins.getItemByFullName("/folder/test-job") instanceof FreeStyleProject);

        // when
        String script2 = "job { name '/folder/different-job' }";
        ExecuteDslScripts builder2 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script2), false, RemovedJobAction.DELETE);
        runBuild(job, builder2);

        // then
        assertTrue(jenkinsRule.jenkins.getItemByFullName("/folder/different-job") instanceof FreeStyleProject);
        assertNull(jenkinsRule.jenkins.getItemByFullName("/folder/test-job"));
    }

    @Test
    public void useTemplateInFolder() throws Exception {
        // setup
        jenkinsRule.jenkins.createProject(Folder.class, "folder");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        jenkinsRule.jenkins.getExtensionList(Descriptor.class).add(new DescriptorImpl());

        String script1 = "job { name '/folder/test-template'; description 'useTemplateInFolder' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        runBuild(job, builder1);

        // when
        String script = "job { name '/folder/test-job'; using '/folder/test-template' }";
        ExecuteDslScripts builder = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script), false, RemovedJobAction.DELETE);
        runBuild(job, builder);

        // then
        assertEquals("useTemplateInFolder", jenkinsRule.jenkins.getItemByFullName("/folder/test-job", AbstractItem.class).getDescription());
    }

    @Test
    public void updateGeneratedFolder() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script1 = "job(type: Folder) { name 'folder' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        runBuild(job, builder1);
        assertEquals(null, jenkinsRule.jenkins.getItemByFullName("folder", AbstractItem.class).getDescription());

        String script2 = "job(type: Folder) { name 'folder'; description 'updateGeneratedFolder' }";
        ExecuteDslScripts builder2 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script2), false, RemovedJobAction.DELETE);
        runBuild(job, builder2);

        // then
        assertEquals("updateGeneratedFolder", jenkinsRule.jenkins.getItemByFullName("folder", AbstractItem.class).getDescription());
    }

    @Test
	public void createFolderUsingTemplate() throws Exception {
		// setup
        jenkinsRule.jenkins.createProject(Folder.class, "folder-template").setDescription("createFolderUsingTemplate");;
        jenkinsRule.jenkins.getExtensionList(Descriptor.class).add(new DescriptorImpl());
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script1 = "job(type: Folder) { name 'folder'; using 'folder-template' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        runBuild(job, builder1);

        // then
        assertEquals("createFolderUsingTemplate", jenkinsRule.jenkins.getItemByFullName("folder", AbstractItem.class).getDescription());
	}

    @Test
	public void removeGeneratedFolder() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script1 = "job(type: Folder) { name 'folder' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        runBuild(job, builder1);

        String script2 = "job { name 'dummy' }";
        ExecuteDslScripts builder2 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script2), false, RemovedJobAction.DELETE);
        runBuild(job, builder2);

        // then
        assertNull(jenkinsRule.jenkins.getItemByFullName("folder"));
	}

    @Test
    public void queueFolderJob() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        ByteArrayOutputStream logStream = new ByteArrayOutputStream();

        // when
        String script1 = "job(type: Folder) { name 'folder' }";
        ExecuteDslScripts builder1 = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script1), false, RemovedJobAction.DELETE);
        FreeStyleBuild build = runBuild(job, builder1);
        EnvVars envVars = build.getEnvironment(StreamTaskListener.fromStdout());
        JenkinsJobManagement jobManagement = new JenkinsJobManagement(new PrintStream(logStream), envVars, build);
        jobManagement.queueJob("folder");

        // then
        assertTrue(logStream.toString().matches("(^|.*\\s)folder(\\s.*|$)\n"));
    }

    private FreeStyleBuild runBuild(FreeStyleProject job, ExecuteDslScripts builder) throws Exception {
        job.getBuildersList().clear();
        job.getBuildersList().add(builder);
        job.onCreatedFromScratch(); // need this to updateTransientActions

        FreeStyleBuild build = job.scheduleBuild2(0).get();

        assertEquals(SUCCESS, build.getResult());
        return build;
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
