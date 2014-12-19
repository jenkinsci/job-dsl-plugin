package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import com.google.common.collect.Lists
import hudson.FilePath
import hudson.maven.MavenModuleSet
import hudson.model.AbstractItem
import hudson.model.AbstractProject
import hudson.model.Action
import hudson.model.FreeStyleBuild
import hudson.model.FreeStyleProject
import hudson.model.Label
import hudson.model.ListView
import hudson.model.View
import hudson.slaves.DumbSlave
import hudson.tasks.Shell
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.dsl.GeneratedView
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Ignore
import spock.lang.Specification

import static hudson.model.Result.SUCCESS
import static javaposse.jobdsl.plugin.RemovedJobAction.DELETE
import static javaposse.jobdsl.plugin.RemovedJobAction.DISABLE
import static javaposse.jobdsl.plugin.RemovedJobAction.IGNORE
import static org.junit.Assert.assertTrue

class ExecuteDslScriptsSpec extends Specification {
    private static final String UTF_8 = 'UTF-8'

    ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()
    AbstractProject project = Mock(AbstractProject)

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    def 'getProjectActions'() {
        when:
        List<? extends Action> actions = Lists.newArrayList(executeDslScripts.getProjectActions(project))

        then:
        actions != null
        actions.size() == 3
        actions[0] instanceof GeneratedJobsAction
        actions[1] instanceof GeneratedViewsAction
        actions[2] instanceof GeneratedConfigFilesAction
    }

    def scheduleBuildOnMasterUsingScriptText() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('true', null, SCRIPT), true, IGNORE)
        )

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
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('true', null, SCRIPT), true, IGNORE)
        )
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
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('false', 'jobs.groovy', null), true, IGNORE)
        )
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
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('false', 'jobs.groovy', null), true, IGNORE)
        )
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
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('false', '**/*.groovy', null), true, IGNORE)
        )
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
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('false', 'jobs.groovy', null), true, IGNORE)
        )
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
        job.buildersList.add(
                new ExecuteDslScripts(
                        new ExecuteDslScripts.ScriptLocation('false', 'mydsl/jobs.groovy', null),
                        true,
                        IGNORE
                )
        )
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
        String script1 = 'job { name "test-job" }'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1), false, DELETE
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job { name "different-job" }'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2), false, DELETE
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
        String script1 = 'job { name "/folder/test-job" }'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1), false, DELETE
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('/folder/test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job { name "/folder/different-job" }'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2), false, DELETE
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
        String script1 = 'job { name "test-job" }'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script1), false, DELETE, LookupStrategy.SEED_JOB
        )
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('/folder/test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job { name "/folder/different-job" }'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script2), false, DELETE, LookupStrategy.SEED_JOB
        )
        runBuild(job, builder2)

        then:
        jenkinsRule.jenkins.getItemByFullName('/folder/different-job') instanceof FreeStyleProject
        jenkinsRule.jenkins.getItemByFullName('/folder/test-job') == null
    }

    def useTemplateInRoot() {
        setup:
        FreeStyleProject template = jenkinsRule.createFreeStyleProject('template')
        String description = 'template project in root'
        template.description = description
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script = 'job {\n name("test-job")\n using("template")\n}'
        ExecuteDslScripts builder = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script), false, DELETE
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
        String script = 'job {\n name("test-job")\n using("/template-folder/template")\n}'
        ExecuteDslScripts builder = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script), false, DELETE
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
        String script = 'job {\n name("test-job")\n using("template")\n}'
        ExecuteDslScripts builder = new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, script), false, DELETE, LookupStrategy.SEED_JOB
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

        build.result == SUCCESS
        build
    }

    def "SeedJobAction is added to created jobs"() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')

        when:
        FreeStyleBuild build = runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n using("template")\n}'))

        then:
        build.result == SUCCESS
        def testJob = jenkinsRule.instance.getItemByFullName('test-job', AbstractProject)
        def action = testJob.getAction(SeedJobAction)
        action.templateJob.name == 'template'
        action.seedJob.name == 'seed'

        def seedReference = jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job')
        seedReference.seedJobName == 'seed'
        seedReference.templateJobName == 'template'
    }

    def "SeedJobAction is added to updated jobs"() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')

        when:
        jenkinsRule.createFreeStyleProject('test-job')

        then:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(SeedJobAction) == null

        when:
        def build1 = runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n}'))

        then:
        build1.result == SUCCESS
        def testJob1 = jenkinsRule.instance.getItemByFullName('test-job', AbstractProject)
        def action1 = testJob1.getAction(SeedJobAction)
        action1.templateJob == null
        action1.seedJob.name == 'seed'
        def seedReference1 = jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job')
        seedReference1.seedJobName == 'seed'
        seedReference1.templateJobName == null

        when:
        def build2 = runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n using("template")\n}'))

        then:
        build2.result == SUCCESS
        def testJob2 = jenkinsRule.instance.getItemByFullName('test-job', AbstractProject)
        def action2 = testJob2.getAction(SeedJobAction)
        action2.templateJob.name == 'template'
        action2.seedJob.name == 'seed'
        def seedReference2 = jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job')
        seedReference2.seedJobName == 'seed'
        seedReference2.templateJobName == 'template'
    }

    def "SeedJobAction is removed from ignored jobs"() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n using("template")\n}'))

        when:
        runBuild(job, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, IGNORE
        ))

        then:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(SeedJobAction) == null
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job') == null
    }

    def "SeedJobAction is removed from disabled jobs"() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n using("template")\n}'))

        when:
        runBuild(job, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, DISABLE
        ))

        then:
        jenkinsRule.instance.getItemByFullName('test-job', AbstractProject).getAction(SeedJobAction) == null
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job') == null
    }

    def "Deleted job is removed from GeneratedJobMap"() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n using("template")\n}'))

        when:
        runBuild(job, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, DELETE
        ))

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job') == null
    }

    @Ignore
    def "Manually deleted job is removed from GeneratedJobMap"() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        jenkinsRule.createFreeStyleProject('template')
        runBuild(job, new ExecuteDslScripts('job {\n name("test-job")\n using("template")\n}'))
        jenkinsRule.instance.getItemByFullName('test-job').delete()

        when:
        runBuild(job, new ExecuteDslScripts(
                new ExecuteDslScripts.ScriptLocation('true', null, '// do nothing'), false, DELETE
        ))

        then:
        jenkinsRule.instance.getDescriptorByType(DescriptorImpl).generatedJobMap.get('test-job') == null
    }

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
        buildAction.modifiedViews != null
        buildAction.modifiedViews.size() == 1
        buildAction.modifiedViews.contains(new GeneratedView('test-view'))

        when:
        GeneratedViewsAction action = job.getAction(GeneratedViewsAction)

        then:
        action != null
        action.findLastGeneratedViews() != null
        action.findLastGeneratedViews().size() == 1
        action.findLastGeneratedViews().contains(new GeneratedView('test-view'))
        action.findAllGeneratedViews() != null
        action.findAllGeneratedViews().size() == 1
        action.findAllGeneratedViews().contains(new GeneratedView('test-view'))
        action.views != null
        action.views.size() == 1
        action.views.contains(jenkinsRule.instance.getView('test-view'))
    }

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
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('true', null, VIEW_SCRIPT), true, IGNORE)
        )
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
        buildAction.modifiedJobs != null
        buildAction.modifiedJobs.size() == 1
        buildAction.modifiedJobs.contains(new GeneratedJob(null, 'test-folder'))

        when:
        GeneratedJobsAction action = job.getAction(GeneratedJobsAction)

        then:
        action != null
        action.findLastGeneratedJobs() != null
        action.findLastGeneratedJobs().size() == 1
        action.findLastGeneratedJobs().contains(new GeneratedJob(null, 'test-folder'))
        action.findAllGeneratedJobs() != null
        action.findAllGeneratedJobs().size() == 1
        action.findAllGeneratedJobs().contains(new GeneratedJob(null, 'test-folder'))
        action.items != null
        action.items.size() == 1
        action.items.contains(jenkinsRule.instance.getItem('test-folder'))
    }

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

    def updateFolderIgnoreChanges() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'test-folder')
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('true', null, FOLDER_SCRIPT), true, IGNORE)
        )
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

    def removeFolder() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('true', null, FOLDER_SCRIPT), true, IGNORE)
        )
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        job.buildersList.clear()
        job.buildersList.add(
                new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation('true', null, '// empty'), true, DELETE)
        )
        freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-folder') == null
    }

    def 'maven pre and post build steps'() {
        setup:
        String mavenPrePostScript = '''job(type: 'Maven') {
    name('maven-job')
    goals('clean install')
    preBuildSteps {
        shell('echo first')
    }
    postBuildSteps {
       shell('echo second')
    }
}'''

        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(
                new ExecuteDslScripts(
                        new ExecuteDslScripts.ScriptLocation('true', null, mavenPrePostScript), true, IGNORE
                )
        )

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

    @Test
    def 'allow empty archive'() {
        setup:
        String emptyArchiveScript = """job {
    name('test-job')
    steps {
        shell("echo 'foo' > test")
    }
    publishers {
        archiveArtifacts('test*')
    }
}"""

        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(
                new ExecuteDslScripts(
                        new ExecuteDslScripts.ScriptLocation('true', null, emptyArchiveScript), true, IGNORE
                )
        )

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    private static final String SCRIPT = """job {
    name('test-job')
}"""

    private static final String UTIL_SCRIPT = '''import util.Util

def u = new Util().getName()

job {
    name(u)
}'''

    private static final String UTIL_CLASS = '''package util

public class Util {
    String getName() { return "test-job" }
}'''

    private static final String JOB_IN_FOLDER_SCRIPT = """folder {
  name('folder-a')
}

job {
  name('folder-a/test-job')
  description('lorem ipsum')
}"""

    private static final String VIEW_SCRIPT = """view {
  name('test-view')
  description('lorem ipsum')
}"""

    private static final String VIEW_IN_FOLDER_SCRIPT = """folder {
  name('folder-a')
}

view {
  name('folder-a/test-view')
  description('lorem ipsum')
}"""

    private static final String FOLDER_SCRIPT = """folder {
  name('test-folder')
  description('lorem ipsum')
}"""

    private static final String FOLDER_IN_FOLDER_SCRIPT = """folder {
  name('folder-a')
}

folder {
  name('folder-a/folder-b')
  description('lorem ipsum')
}"""
}
