package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.FilePath;
import hudson.model.AbstractItem;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.model.ListView;
import hudson.model.View;
import hudson.slaves.DumbSlave;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.GeneratedView;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;

import static hudson.model.Result.SUCCESS;
import static javaposse.jobdsl.plugin.RemovedJobAction.IGNORE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void useTemplateInRoot() throws Exception {
        // setup
        FreeStyleProject template = jenkinsRule.createFreeStyleProject("template");
        String description = "template project in root";
        template.setDescription(description);
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script = "job { name('test-job'); using('template') }";
        ExecuteDslScripts builder = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script), false, RemovedJobAction.DELETE);
        FreeStyleBuild build = runBuild(job, builder);

        // then
        assertEquals(SUCCESS, build.getResult());
        assertEquals(jenkinsRule.jenkins.getItemByFullName("test-job", FreeStyleProject.class).getDescription(), description);
    }

    @Test
    public void useTemplateInFolder() throws Exception {
        // setup
        Folder folder = jenkinsRule.jenkins.createProject(Folder.class, "template-folder");
        FreeStyleProject template = folder.createProject(FreeStyleProject.class, "template");
        String description = "template project in a folder";
        template.setDescription(description);
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");

        // when
        String script = "job { name('test-job'); using('/template-folder/template') }";
        ExecuteDslScripts builder = new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, script), false, RemovedJobAction.DELETE);
        FreeStyleBuild build = runBuild(job, builder);

        // then
        assertEquals(SUCCESS, build.getResult());
        assertEquals(jenkinsRule.jenkins.getItemByFullName("test-job", FreeStyleProject.class).getDescription(), description);
    }

    private FreeStyleBuild runBuild(FreeStyleProject job, ExecuteDslScripts builder) throws Exception {
        job.getBuildersList().clear();
        job.getBuildersList().add(builder);
        job.onCreatedFromScratch(); // need this to updateTransientActions

        FreeStyleBuild build = job.scheduleBuild2(0).get();

        assertEquals(SUCCESS, build.getResult());
        return build;
    }

    @Test
    public void createJobInFolder() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(JOB_IN_FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItemByFullName("folder-a") instanceof Folder);
        assertTrue(jenkinsRule.getInstance().getItemByFullName("folder-a/test-job") instanceof FreeStyleProject);
    }

    @Test
    public void updateJobInFolder() throws Exception {
        // setup
        jenkinsRule.getInstance().createProject(Folder.class, "folder-a").createProject(FreeStyleProject.class, "test-job");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(JOB_IN_FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        AbstractItem item = (AbstractItem) jenkinsRule.getInstance().getItemByFullName("folder-a/test-job");

        // then
        assertTrue(item instanceof FreeStyleProject);
        assertEquals("lorem ipsum", item.getDescription());
    }

    @Test
    public void createView() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(VIEW_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getView("test-view") instanceof ListView);

        // when
        GeneratedViewsBuildAction buildAction = freeStyleBuild.getAction(GeneratedViewsBuildAction.class);

        // then
        assertNotNull(buildAction);
        assertNotNull(buildAction.getModifiedViews());
        assertEquals(1, buildAction.getModifiedViews().size());
        assertTrue(buildAction.getModifiedViews().contains(new GeneratedView("test-view")));

        // when
        GeneratedViewsAction action = job.getAction(GeneratedViewsAction.class);

        // then
        assertNotNull(action);
        assertNotNull(action.findLastGeneratedViews());
        assertEquals(1, action.findLastGeneratedViews().size());
        assertTrue(action.findLastGeneratedViews().contains(new GeneratedView("test-view")));
        assertNotNull(action.findAllGeneratedViews());
        assertEquals(1, action.findAllGeneratedViews().size());
        assertTrue(action.findAllGeneratedViews().contains(new GeneratedView("test-view")));
        assertNotNull(action.getViews());
        assertEquals(1, action.getViews().size());
        assertTrue(action.getViews().contains(jenkinsRule.getInstance().getView("test-view")));
    }

    @Test
    public void createViewInFolder() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(VIEW_IN_FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItemByFullName("folder-a") instanceof Folder);
        Folder folder = (Folder) jenkinsRule.getInstance().getItemByFullName("folder-a");
        assertTrue(folder.getView("test-view") instanceof ListView);
    }

    @Test
    public void updateView() throws Exception {
        // setup
        jenkinsRule.getInstance().addView(new ListView("test-view"));
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(VIEW_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        View view = jenkinsRule.getInstance().getView("test-view");

        // then
        assertTrue(view instanceof ListView);
        assertEquals("lorem ipsum", view.getDescription());
    }

    @Test
    public void updateViewInFolder() throws Exception {
        // setup
        jenkinsRule.getInstance().createProject(Folder.class, "folder-a").addView(new ListView("test-view"));
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(VIEW_IN_FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        View view = jenkinsRule.getInstance().getItemByFullName("folder-a", Folder.class).getView("test-view");

        // then
        assertTrue(view instanceof ListView);
        assertEquals("lorem ipsum", view.getDescription());
    }

    @Test
    public void updateViewIgnoreChanges() throws Exception {
        // setup
        jenkinsRule.getInstance().addView(new ListView("test-view"));
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, VIEW_SCRIPT), true, IGNORE));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        View view = jenkinsRule.getInstance().getView("test-view");

        // then
        assertTrue(view instanceof ListView);
        assertNull(view.getDescription());
    }

    @Test
    public void createFolder() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItem("test-folder") instanceof Folder);

        // when
        GeneratedJobsBuildAction buildAction = freeStyleBuild.getAction(GeneratedJobsBuildAction.class);

        // then
        assertNotNull(buildAction);
        assertNotNull(buildAction.getModifiedJobs());
        assertEquals(1, buildAction.getModifiedJobs().size());
        assertTrue(buildAction.getModifiedJobs().contains(new GeneratedJob(null, "test-folder", true)));

        // when
        GeneratedJobsAction action = job.getAction(GeneratedJobsAction.class);

        // then
        assertNotNull(action);
        assertNotNull(action.findLastGeneratedJobs());
        assertEquals(1, action.findLastGeneratedJobs().size());
        assertTrue(action.findLastGeneratedJobs().contains(new GeneratedJob(null, "test-folder", true)));
        assertNotNull(action.findAllGeneratedJobs());
        assertEquals(1, action.findAllGeneratedJobs().size());
        assertTrue(action.findAllGeneratedJobs().contains(new GeneratedJob(null, "test-folder", true)));
        assertNotNull(action.getItems());
        assertEquals(1, action.getItems().size());
        assertTrue(action.getItems().contains(jenkinsRule.getInstance().getItem("test-folder")));
    }

    @Test
    public void createFolderInFolder() throws Exception {
        // setup
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(FOLDER_IN_FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());
        assertTrue(jenkinsRule.getInstance().getItemByFullName("folder-a") instanceof Folder);
        assertTrue(jenkinsRule.getInstance().getItemByFullName("folder-a/folder-b") instanceof Folder);
    }

    @Test
    public void updateFolder() throws Exception {
        // setup
        jenkinsRule.getInstance().createProject(Folder.class, "test-folder");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        AbstractItem item = (AbstractItem) jenkinsRule.getInstance().getItem("test-folder");

        // then
        assertTrue(item instanceof Folder);
        assertEquals("lorem ipsum", item.getDescription());
    }

    @Test
    public void updateFolderInFolder() throws Exception {
        // setup
        jenkinsRule.getInstance().createProject(Folder.class, "folder-a").createProject(Folder.class, "folder-b");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(FOLDER_IN_FOLDER_SCRIPT));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        AbstractItem item = (AbstractItem) jenkinsRule.getInstance().getItemByFullName("folder-a/folder-b");

        // then
        assertTrue(item instanceof Folder);
        assertEquals("lorem ipsum", item.getDescription());
    }

    @Test
    public void updateFolderIgnoreChanges() throws Exception {
        // setup
        jenkinsRule.getInstance().createProject(Folder.class, "test-folder");
        FreeStyleProject job = jenkinsRule.createFreeStyleProject("seed");
        job.getBuildersList().add(new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation("true", null, FOLDER_SCRIPT), true, IGNORE));
        job.onCreatedFromScratch();

        // when
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get();

        // then
        assertEquals(SUCCESS, freeStyleBuild.getResult());

        // when
        AbstractItem item = (AbstractItem) jenkinsRule.getInstance().getItem("test-folder");

        // then
        assertTrue(item instanceof Folder);
        assertNull(item.getDescription());
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

    private static final String JOB_IN_FOLDER_SCRIPT = "" +
            "folder {\n" +
            "  name('folder-a')\n" +
            "}\n\n" +
            "job {\n" +
            "  name('folder-a/test-job')\n" +
            "  description('lorem ipsum')\n" +
            "}";

    private static final String VIEW_SCRIPT = "" +
            "view {\n" +
            "  name('test-view')\n" +
            "  description('lorem ipsum')\n" +
            "}";

    private static final String VIEW_IN_FOLDER_SCRIPT = "" +
            "folder {\n" +
            "  name('folder-a')\n" +
            "}\n\n" +
            "view {\n" +
            "  name('folder-a/test-view')\n" +
            "  description('lorem ipsum')\n" +
            "}";

    private static final String FOLDER_SCRIPT = "" +
            "folder {\n" +
            "  name('test-folder')\n" +
            "  description('lorem ipsum')\n" +
            "}";

    private static final String FOLDER_IN_FOLDER_SCRIPT = "" +
            "folder {\n" +
            "  name('folder-a')\n" +
            "}\n\n" +
            "folder {\n" +
            "  name('folder-a/folder-b')\n" +
            "  description('lorem ipsum')\n" +
            "}";
}
