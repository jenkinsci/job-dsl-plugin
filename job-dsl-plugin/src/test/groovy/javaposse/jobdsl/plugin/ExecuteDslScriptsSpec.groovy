package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.FilePath
import hudson.maven.MavenModuleSet
import hudson.model.AbstractItem
import hudson.model.AbstractProject
import hudson.model.Action
import hudson.model.FreeStyleBuild
import hudson.model.FreeStyleProject
import hudson.model.Items
import hudson.model.Label
import hudson.model.ListView
import hudson.model.View
import hudson.slaves.DumbSlave
import hudson.tasks.Shell
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.dsl.GeneratedView
import javaposse.jobdsl.plugin.actions.GeneratedConfigFilesAction
import javaposse.jobdsl.plugin.actions.GeneratedJobsAction
import javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction
import javaposse.jobdsl.plugin.actions.GeneratedUserContentsAction
import javaposse.jobdsl.plugin.actions.GeneratedViewsAction
import javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction
import javaposse.jobdsl.plugin.actions.SeedJobAction
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import org.jvnet.hudson.test.recipes.WithPlugin
import spock.lang.Specification

import static hudson.model.Result.SUCCESS
import static org.junit.Assert.assertTrue

class ExecuteDslScriptsSpec extends Specification {
    private static final String UTF_8 = 'UTF-8'

    ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()
    AbstractProject project = Mock(AbstractProject)

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @WithoutJenkins
    def 'getProjectActions'() {
        when:
        List<? extends Action> actions = new ArrayList<? extends Action>(executeDslScripts.getProjectActions(project))

        then:
        actions != null
        actions.size() == 4
        actions[0] instanceof GeneratedJobsAction
        actions[1] instanceof GeneratedViewsAction
        actions[2] instanceof GeneratedConfigFilesAction
        actions[3] instanceof GeneratedUserContentsAction
    }

    def scheduleBuildOnMasterUsingScriptText() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, SCRIPT), true, RemovedJobAction.IGNORE
        ))

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def scheduleBuildOnSlaveUsingScriptText() {
        setup:
        jenkinsRule.createSlave('Node1', 'label1', null)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, SCRIPT), true, RemovedJobAction.IGNORE
        ))
        job.assignedLabel = Label.get('label1')

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def scheduleBuildOnMasterUsingScriptLocation() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('false', 'jobs.groovy', null), true, RemovedJobAction.IGNORE
        ))
        jenkinsRule.instance.getWorkspaceFor(job).child('jobs.groovy').write(SCRIPT, UTF_8)

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def scheduleBuildOnSlaveUsingScriptLocation() {
        setup:
        DumbSlave slave = jenkinsRule.createSlave('Node1', 'label1', null)
        new FilePath(new File(slave.remoteFS)).child('workspace/seed/jobs.groovy').write(SCRIPT, UTF_8)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('false', 'jobs.groovy', null), true, RemovedJobAction.IGNORE
        ))
        job.assignedLabel = Label.get('label1')

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def scheduleBuildOnSlaveUsingGlob() {
        setup:
        DumbSlave slave = jenkinsRule.createSlave('Node2', 'label2', null)
        new FilePath(new File(slave.remoteFS)).child('workspace/seed/dslscripts/jobs.groovy').write(SCRIPT, UTF_8)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('false', '**/*.groovy', null), true, RemovedJobAction.IGNORE
        ))
        job.assignedLabel = Label.get('label2')

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def scheduleBuildOnSlaveUsingGroovyEngineLoading() {
        setup:
        DumbSlave slave = jenkinsRule.createSlave('Node3', 'label3', null)
        FilePath remoteFS = new FilePath(new File(slave.remoteFS))
        remoteFS.child('workspace/groovyengine/jobs.groovy').write(UTIL_SCRIPT, UTF_8)
        remoteFS.child('workspace/groovyengine/util/Util.groovy').write(UTIL_CLASS, UTF_8)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('groovyengine')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('false', 'jobs.groovy', null), true, RemovedJobAction.IGNORE
        ))
        job.assignedLabel = Label.get('label3')

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def scheduleBuildOnSlaveUsingGroovyEngineInSubdirectory() {
        setup:
        DumbSlave slave = jenkinsRule.createSlave('Node4', 'label4', null)
        FilePath remoteFS = new FilePath(new File(slave.remoteFS))
        remoteFS.child('workspace/groovyengine/mydsl/jobs.groovy').write(UTIL_SCRIPT, UTF_8)
        remoteFS.child('workspace/groovyengine/mydsl/util/Util.groovy').write(UTIL_CLASS, UTF_8)
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('groovyengine')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('false', 'mydsl/jobs.groovy', null), true, RemovedJobAction.IGNORE
        ))
        job.assignedLabel = Label.get('label4')

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def deleteJob() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'job("test-job")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1), false, RemovedJobAction.DELETE
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2), false, RemovedJobAction.DELETE
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getItemByFullName('different-job') instanceof FreeStyleProject
        jenkinsRule.jenkins.getItemByFullName('test-job') == null
    }

    def deleteJobInFolder() {
        setup:
        jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'job("/folder/test-job")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1), false, RemovedJobAction.DELETE
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('/folder/test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("/folder/different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2), false, RemovedJobAction.DELETE
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getItemByFullName('/folder/different-job') instanceof FreeStyleProject
        jenkinsRule.jenkins.getItemByFullName('/folder/test-job') == null
    }

    def deleteJobRelative() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject job = folder.createProject(FreeStyleProject, 'seed')

        when:
        String script1 = 'job("test-job")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1),
                false,
                RemovedJobAction.DELETE,
                LookupStrategy.SEED_JOB
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('/folder/test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("/folder/different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2),
                false,
                RemovedJobAction.DELETE,
                LookupStrategy.SEED_JOB
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getItemByFullName('/folder/different-job') instanceof FreeStyleProject
        jenkinsRule.jenkins.getItemByFullName('/folder/test-job') == null
    }

    def 'only use last build to calculate items to be deleted'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'job("test-job")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1), false, RemovedJobAction.DELETE
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2), false, RemovedJobAction.DELETE
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getItemByFullName('different-job') instanceof FreeStyleProject
        jenkinsRule.jenkins.getItemByFullName('test-job') == null

        when:
        jenkinsRule.createFreeStyleProject('test-job')
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getItemByFullName('different-job') instanceof FreeStyleProject
        jenkinsRule.jenkins.getItemByFullName('test-job') instanceof FreeStyleProject
    }

    def useTemplateInRoot() {
        setup:
        FreeStyleProject template = jenkinsRule.createFreeStyleProject('template')
        String description = 'template project in root'
        template.description = description
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script = 'job("test-job") {\n using("template")\n}'
        ExecuteDslScripts builder = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script), false, RemovedJobAction.DELETE
        )
        FreeStyleBuild build = runBuild(job, builder)

        then:
        build.result == SUCCESS
        jenkinsRule.jenkins.getItemByFullName('test-job', FreeStyleProject).description == description
    }

    def useTemplateInFolder() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'template-folder')
        FreeStyleProject template = folder.createProject(FreeStyleProject, 'template')
        String description = 'template project in a folder'
        template.description = description
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script = 'job("test-job") {\n using("/template-folder/template")\n}'
        ExecuteDslScripts builder = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script), false, RemovedJobAction.DELETE
        )
        FreeStyleBuild build = runBuild(job, builder)

        then:
        build.result == SUCCESS
        jenkinsRule.jenkins.getItemByFullName('test-job', FreeStyleProject).description == description
    }

    def useRelativeTemplate() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject template = folder.createProject(FreeStyleProject, 'template')
        String description = 'relative template'
        template.description = description
        FreeStyleProject job = folder.createProject(FreeStyleProject, 'seed')

        when:
        String script = 'job("test-job") {\n using("template")\n}'
        ExecuteDslScripts builder = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script),
                false,
                RemovedJobAction.DELETE,
                LookupStrategy.SEED_JOB
        )
        FreeStyleBuild build = runBuild(job, builder)

        then:
        build.result == SUCCESS
        jenkinsRule.jenkins.getItemByFullName('/folder/test-job', FreeStyleProject).description == description
    }

    private static FreeStyleBuild runBuild(FreeStyleProject job, ExecuteDslScripts builder) {
        job.buildersList.clear()
        job.buildersList.add(builder)
        job.onCreatedFromScratch() // need this to updateTransientActions

        FreeStyleBuild build = job.scheduleBuild2(0).get()

        assert build.result == SUCCESS
        build
    }

    def "SeedJobAction is added to created jobs"() {
        setup:
        DescriptorImpl descriptor = jenkinsRule.instance.getDescriptorByType(DescriptorImpl)
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        FreeStyleProject template = jenkinsRule.createFreeStyleProject('template')

        when:
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n using("template")\n}'))

        then:
        AbstractProject testJob = jenkinsRule.instance.getItemByFullName('test-job', AbstractProject)
        SeedJobAction action = testJob.getAction(SeedJobAction)
        action.templateJob == template
        action.seedJob == seedJob

        SeedReference seedReference = descriptor.generatedJobMap.get('test-job')
        seedReference.seedJobName == 'seed'
        seedReference.templateJobName == 'template'
    }

    def "SeedJobAction is added to updated jobs"() {
        setup:
        DescriptorImpl descriptor = jenkinsRule.instance.getDescriptorByType(DescriptorImpl)
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        FreeStyleProject template = jenkinsRule.createFreeStyleProject('template')
        jenkinsRule.createFreeStyleProject('test-job')

        when:
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n using("template")\n}'))

        then:
        AbstractProject testJob = jenkinsRule.instance.getItemByFullName('test-job', AbstractProject)
        SeedJobAction action = testJob.getAction(SeedJobAction)
        action.templateJob == template
        action.seedJob == seedJob
        SeedReference seedReference = descriptor.generatedJobMap.get('test-job')
        seedReference.seedJobName == 'seed'
        seedReference.templateJobName == 'template'
    }

    def "SeedJobAction is removed from ignored jobs"() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n using("template")\n}'))

        when:
        runBuild(seedJob, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, RemovedJobAction.IGNORE
        ))

        then:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(SeedJobAction) == null
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job') == null
    }

    def "SeedJobAction is removed from disabled jobs"() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n using("template")\n}'))

        when:
        runBuild(seedJob, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, RemovedJobAction.DISABLE
        ))

        then:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(SeedJobAction) == null
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job') == null
    }

    def "Deleted job is removed from GeneratedJobMap"() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n using("template")\n}'))

        when:
        runBuild(seedJob, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, RemovedJobAction.DELETE
        ))

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.size() == 0
    }

    def "Manually deleted job is removed from GeneratedJobMap"() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n using("template")\n}'))

        when:
        jenkinsRule.instance.getItemByFullName('test-job').delete()

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.size() == 0
    }

    def "Manually deleted job in folder is removed from GeneratedJobMap"() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("/folder/test-job") {\n using("template")\n}'))

        when:
        folder.delete()

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.size() == 0
    }

    def "Renamed job is removed from GeneratedJobMap"() {
        setup:
        jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("/folder/test-job") {\n using("template")\n}'))

        when:
        (jenkinsRule.instance.getItemByFullName('/folder/test-job') as AbstractProject).renameTo('renamed')

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.size() == 0
        jenkinsRule.instance.getItemByFullName('/folder/renamed', AbstractProject).getAction(SeedJobAction) == null
    }

    def "Moved job is removed from GeneratedJobMap"() {
        setup:
        jenkinsRule.jenkins.createProject(Folder, 'folder')
        Folder target = jenkinsRule.jenkins.createProject(Folder, 'target')
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(seedJob, new ExecuteDslScripts('job("/folder/test-job") {\n using("template")\n}'))

        when:
        Items.move(jenkinsRule.instance.getItemByFullName('/folder/test-job', FreeStyleProject), target)

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.size() == 0
        jenkinsRule.instance.getItemByFullName('/target/test-job', AbstractProject).getAction(SeedJobAction) == null
    }

    def "Warn when job is updated manually"() {
        when:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        runBuild(seedJob, new ExecuteDslScripts('job("test-job") {\n description("abc")}'))

        then:
        !jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(
                SeedJobAction).isConfigChanged()

        when:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).description = 'desc'

        then:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(
                SeedJobAction).isConfigChanged()
    }

    @WithPlugin('cloudbees-folder.hpi')
    def createJobInFolder() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(JOB_IN_FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItemByFullName('folder-a') instanceof Folder
        jenkinsRule.instance.getItemByFullName('folder-a/test-job') instanceof FreeStyleProject
    }

    @WithPlugin('cloudbees-folder.hpi')
    def updateJobInFolder() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'folder-a').createProject(FreeStyleProject, 'test-job')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(JOB_IN_FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        AbstractItem item = (AbstractItem) jenkinsRule.instance.getItemByFullName('folder-a/test-job')

        then:
        item instanceof FreeStyleProject
        item.description == 'lorem ipsum'
    }

    def createView() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(VIEW_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        assertTrue(jenkinsRule.instance.getView('test-view') instanceof ListView)

        when:
        GeneratedViewsBuildAction buildAction = freeStyleBuild.getAction(GeneratedViewsBuildAction)

        then:
        buildAction != null
        buildAction.modifiedObjects != null
        buildAction.modifiedObjects.size() == 1
        buildAction.modifiedObjects.contains(new GeneratedView('test-view'))

        when:
        GeneratedViewsAction action = job.getAction(GeneratedViewsAction)

        then:
        action != null
        action.findLastGeneratedObjects() != null
        action.findLastGeneratedObjects().size() == 1
        action.findLastGeneratedObjects().contains(new GeneratedView('test-view'))
        action.views != null
        action.views.size() == 1
        action.views.contains(jenkinsRule.instance.getView('test-view'))
    }

    @WithPlugin('cloudbees-folder.hpi')
    def createViewInFolder() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(VIEW_IN_FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItemByFullName('folder-a') instanceof Folder
        Folder folder = (Folder) jenkinsRule.instance.getItemByFullName('folder-a')
        folder.getView('test-view') instanceof ListView
    }

    def updateView() {
        setup:
        jenkinsRule.instance.addView(new ListView('test-view'))
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(VIEW_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        View view = jenkinsRule.instance.getView('test-view')

        then:
        view instanceof ListView
        view.description == 'lorem ipsum'
    }

    @WithPlugin('cloudbees-folder.hpi')
    def updateViewInFolder() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'folder-a').addView(new ListView('test-view'))
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(VIEW_IN_FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        View view = jenkinsRule.instance.getItemByFullName('folder-a', Folder).getView('test-view')

        then:
        view instanceof ListView
        view.description == 'lorem ipsum'
    }

    def updateViewIgnoreChanges() {
        setup:
        jenkinsRule.instance.addView(new ListView('test-view'))
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, VIEW_SCRIPT), true, RemovedJobAction.IGNORE
        ))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        View view = jenkinsRule.instance.getView('test-view')

        then:
        view instanceof ListView
        view.description == null
    }

    def deleteView() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'listView("test-view")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.JENKINS_ROOT
        )
        runBuild(job, builder1)

        then:
        jenkinsRule.instance.getView('test-view') instanceof ListView

        when:
        String script2 = 'listView("different-view")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.JENKINS_ROOT
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getView('different-view') instanceof ListView
        jenkinsRule.jenkins.getView('test-view') == null
    }

    def deleteViewInFolder() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'listView("/folder/test-view")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.JENKINS_ROOT
        )
        runBuild(job, builder1)

        then:
        folder.getView('test-view') instanceof ListView

        when:
        String script2 = 'listView("/folder/different-view")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.JENKINS_ROOT
        )
        runBuild(job, builder2)

        then:
        folder.getView('different-view') instanceof ListView
        folder.getView('test-view') == null
    }

    @WithPlugin('cloudbees-folder.hpi')
    def 'delete view in folder after folder has been deleted'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'folder("folder")\nlistView("/folder/test-view")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.JENKINS_ROOT
        )
        runBuild(job, builder1)

        then:
        Folder folder = jenkinsRule.instance.getItemByFullName('folder', Folder)
        folder != null
        folder.getView('test-view') instanceof ListView

        when:
        String script2 = '// no-op'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.JENKINS_ROOT
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.instance.getItemByFullName('folder', Folder) == null
    }

    def deleteViewRelative() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        FreeStyleProject job = folder.createProject(FreeStyleProject, 'seed')

        when:
        String script1 = 'listView("test-view")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.SEED_JOB
        )
        runBuild(job, builder1)

        then:
        folder.getView('test-view') instanceof ListView

        when:
        String script2 = 'listView("different-view")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2),
                false,
                RemovedJobAction.DELETE,
                RemovedViewAction.DELETE,
                LookupStrategy.SEED_JOB
        )
        runBuild(job, builder2)

        then:
        folder.getView('different-view') instanceof ListView
        folder.getView('test-view') == null
    }

    @WithPlugin('cloudbees-folder.hpi')
    def createFolder() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        assertTrue(jenkinsRule.instance.getItem('test-folder') instanceof Folder)

        when:
        GeneratedJobsBuildAction buildAction = freeStyleBuild.getAction(GeneratedJobsBuildAction)

        then:
        buildAction != null
        buildAction.modifiedObjects != null
        buildAction.modifiedObjects.size() == 1
        buildAction.modifiedObjects.contains(new GeneratedJob(null, 'test-folder'))

        when:
        GeneratedJobsAction action = job.getAction(GeneratedJobsAction)

        then:
        action != null
        action.findLastGeneratedObjects() != null
        action.findLastGeneratedObjects().size() == 1
        action.findLastGeneratedObjects().contains(new GeneratedJob(null, 'test-folder'))
        action.items != null
        action.items.size() == 1
        action.items.contains(jenkinsRule.instance.getItem('test-folder'))
    }

    @WithPlugin('cloudbees-folder.hpi')
    def createFolderInFolder() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(FOLDER_IN_FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItemByFullName('folder-a') instanceof Folder
        jenkinsRule.instance.getItemByFullName('folder-a/folder-b') instanceof Folder
    }

    @WithPlugin('cloudbees-folder.hpi')
    def updateFolder() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'test-folder')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        AbstractItem item = (AbstractItem) jenkinsRule.instance.getItem('test-folder')

        then:
        item instanceof Folder
        item.description == 'lorem ipsum'
    }

    @WithPlugin('cloudbees-folder.hpi')
    def updateFolderInFolder() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'folder-a').createProject(Folder, 'folder-b')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(FOLDER_IN_FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        AbstractItem item = (AbstractItem) jenkinsRule.instance.getItemByFullName('folder-a/folder-b')

        then:
        item instanceof Folder
        item.description == 'lorem ipsum'
    }

    @WithPlugin('cloudbees-folder.hpi')
    def updateFolderIgnoreChanges() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'test-folder')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, FOLDER_SCRIPT), true, RemovedJobAction.IGNORE
        ))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        AbstractItem item = (AbstractItem) jenkinsRule.instance.getItem('test-folder')

        then:
        item instanceof Folder
        item.description == null
    }

    @WithPlugin('cloudbees-folder.hpi')
    def removeFolder() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, FOLDER_SCRIPT), true, RemovedJobAction.IGNORE
        ))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        job.buildersList.clear()
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// empty'), true, RemovedJobAction.DELETE
        ))
        freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-folder') == null
    }

    def 'maven pre and post build steps'() {
        setup:
        String mavenPrePostScript = '''mavenJob('maven-job') {
    goals('clean install')
    preBuildSteps {
        shell('echo first')
    }
    postBuildSteps {
       shell('echo second')
    }
}'''

        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, mavenPrePostScript), true, RemovedJobAction.IGNORE
        ))

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('maven-job') instanceof MavenModuleSet
        MavenModuleSet mavenJob = (MavenModuleSet) jenkinsRule.instance.getItem('maven-job')
        mavenJob.prebuilders.size() == 1
        mavenJob.postbuilders.size() == 1
        mavenJob.prebuilders.get(0) instanceof Shell
        mavenJob.postbuilders.get(0) instanceof Shell
    }

    def 'allow empty archive'() {
        setup:
        String emptyArchiveScript = """job('test-job') {
    steps {
        shell("echo 'foo' > test")
    }
    publishers {
        archiveArtifacts('test*')
    }
}"""

        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, emptyArchiveScript), true, RemovedJobAction.IGNORE
        ))

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def 'extension is used'() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        seedJob.buildersList.add(new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, EXTENSION_SCRIPT), true, RemovedJobAction.IGNORE
        ))

        when:
        FreeStyleBuild freeStyleBuild = seedJob.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        FreeStyleProject job = jenkinsRule.instance.getItem('example-extension') as FreeStyleProject
        job != null
        ExampleJobDslExtension.SomeValueObject jobProperty = job.getProperty(ExampleJobDslExtension.SomeValueObject)
        jobProperty != null
        jobProperty.value == 'foo'
        File testFile = new File(job.rootDir, 'foo.json')
        testFile.exists()
        testFile.text == 'bar'
    }

    def 'user content is created'() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        seedJob.scheduleBuild2(0).get() // run a build to create a workspace
        seedJob.someWorkspace.child('foo.txt').write('lorem ipsum', 'UTF-8')
        seedJob.buildersList.add(new ExecuteDslScripts("userContent('foo.txt', streamFileFromWorkspace('foo.txt'))"))

        when:
        FreeStyleBuild freeStyleBuild = seedJob.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.rootPath.child('userContent').child('foo.txt').exists()
        jenkinsRule.instance.rootPath.child('userContent').child('foo.txt').readToString().trim() == 'lorem ipsum'
    }

    def 'deprecation warning in DSL script'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(this.class.getResourceAsStream('deprecation.groovy').text))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.getLog(25).join('\n') =~ /Warning: \(script, line 4\) job is deprecated/
    }

    private static final String SCRIPT = """job('test-job') {
}"""

    private static final String EXTENSION_SCRIPT = """job('example-extension') {
    properties {
        example('foo', 'bar')
    }
}"""

    private static final String UTIL_SCRIPT = '''import util.Util

def u = new Util().getName()

job(u) {
}'''

    private static final String UTIL_CLASS = '''package util

public class Util {
    String getName() { return "test-job" }
}'''

    private static final String JOB_IN_FOLDER_SCRIPT = """folder('folder-a') {
}

job('folder-a/test-job') {
  description('lorem ipsum')
}"""

    private static final String VIEW_SCRIPT = """listView('test-view') {
  description('lorem ipsum')
}"""

    private static final String VIEW_IN_FOLDER_SCRIPT = """folder('folder-a') {
}

listView('folder-a/test-view') {
  description('lorem ipsum')
}"""

    private static final String FOLDER_SCRIPT = """folder('test-folder') {
  description('lorem ipsum')
}"""

    private static final String FOLDER_IN_FOLDER_SCRIPT = """folder('folder-a') {
}

folder('folder-a/folder-b') {
  description('lorem ipsum')
}"""
}
