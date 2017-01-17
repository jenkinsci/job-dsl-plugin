package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.FilePath
import hudson.model.AbstractItem
import hudson.model.AbstractProject
import hudson.model.FreeStyleBuild
import hudson.model.FreeStyleProject
import hudson.model.Items
import hudson.model.Label
import hudson.model.ListView
import hudson.model.Project
import hudson.model.Result
import hudson.model.Run
import hudson.model.View
import hudson.slaves.DumbSlave
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.dsl.GeneratedView
import javaposse.jobdsl.plugin.actions.GeneratedJobsAction
import javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction
import javaposse.jobdsl.plugin.actions.GeneratedViewsAction
import javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction
import javaposse.jobdsl.plugin.actions.SeedJobAction
import javaposse.jobdsl.plugin.fixtures.ExampleJobDslExtension
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles
import org.jenkinsci.plugins.configfiles.custom.CustomConfig
import org.jenkinsci.plugins.managedscripts.PowerShellConfig
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins
import spock.lang.Specification
import spock.lang.Unroll

import static hudson.model.Result.FAILURE
import static hudson.model.Result.SUCCESS
import static hudson.model.Result.UNSTABLE
import static org.junit.Assert.assertTrue

@Unroll
class ExecuteDslScriptsSpec extends Specification {
    private static final String UTF_8 = 'UTF-8'

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @WithoutJenkins
    def 'targets'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        executeDslScripts.targets == null
        executeDslScripts.usingScriptText

        when:
        executeDslScripts.targets = 'foo'

        then:
        executeDslScripts.targets == 'foo'
        !executeDslScripts.usingScriptText

        when:
        executeDslScripts.useScriptText = true

        then:
        executeDslScripts.targets == null
        executeDslScripts.usingScriptText

        when:
        executeDslScripts.useScriptText = false

        then:
        executeDslScripts.targets == 'foo'
        !executeDslScripts.usingScriptText

        when:
        executeDslScripts.targets = '  '

        then:
        executeDslScripts.targets == null
        !executeDslScripts.usingScriptText
    }

    @WithoutJenkins
    def 'script text'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        executeDslScripts.scriptText == null
        executeDslScripts.usingScriptText

        when:
        executeDslScripts.scriptText = 'foo'
        executeDslScripts.usingScriptText

        then:
        executeDslScripts.scriptText == 'foo'
        executeDslScripts.usingScriptText

        when:
        executeDslScripts.useScriptText = true

        then:
        executeDslScripts.scriptText == 'foo'
        executeDslScripts.usingScriptText

        when:
        executeDslScripts.useScriptText = false

        then:
        executeDslScripts.scriptText == null
        !executeDslScripts.usingScriptText

        when:
        executeDslScripts.scriptText = '  '
        executeDslScripts.useScriptText = true

        then:
        executeDslScripts.scriptText == null
        executeDslScripts.usingScriptText
    }

    @WithoutJenkins
    def 'ignore missing files'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        !executeDslScripts.ignoreMissingFiles

        when:
        executeDslScripts.ignoreMissingFiles = true

        then:
        !executeDslScripts.ignoreMissingFiles

        when:
        executeDslScripts.targets = 'foo'

        then:
        executeDslScripts.ignoreMissingFiles

        when:
        executeDslScripts.useScriptText = true

        then:
        !executeDslScripts.ignoreMissingFiles

        when:
        executeDslScripts.useScriptText = false

        then:
        executeDslScripts.ignoreMissingFiles
    }

    @WithoutJenkins
    def 'ignore existing'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        !executeDslScripts.ignoreExisting

        when:
        executeDslScripts.ignoreExisting = true

        then:
        executeDslScripts.ignoreExisting

        when:
        executeDslScripts.ignoreExisting = false

        then:
        !executeDslScripts.ignoreExisting
    }

    @WithoutJenkins
    def 'removed job action'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        executeDslScripts.removedJobAction == RemovedJobAction.IGNORE

        when:
        executeDslScripts.removedJobAction = RemovedJobAction.DELETE

        then:
        executeDslScripts.removedJobAction == RemovedJobAction.DELETE
    }

    @WithoutJenkins
    def 'removed view action'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        executeDslScripts.removedViewAction == RemovedViewAction.IGNORE

        when:
        executeDslScripts.removedViewAction = RemovedViewAction.DELETE

        then:
        executeDslScripts.removedViewAction == RemovedViewAction.DELETE
    }

    @WithoutJenkins
    def 'lookup strategy'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        executeDslScripts.lookupStrategy == LookupStrategy.JENKINS_ROOT

        when:
        executeDslScripts.lookupStrategy = LookupStrategy.SEED_JOB

        then:
        executeDslScripts.lookupStrategy == LookupStrategy.SEED_JOB

        when:
        executeDslScripts.lookupStrategy = null

        then:
        executeDslScripts.lookupStrategy == LookupStrategy.JENKINS_ROOT
    }

    @WithoutJenkins
    def 'additional classpath'() {
        setup:
        ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

        expect:
        executeDslScripts.additionalClasspath == null

        when:
        executeDslScripts.additionalClasspath = 'foo'

        then:
        executeDslScripts.additionalClasspath == 'foo'

        when:
        executeDslScripts.additionalClasspath = '   '

        then:
        executeDslScripts.additionalClasspath == null
    }

    def scheduleBuildOnMasterUsingScriptText() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(SCRIPT))

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
        job.buildersList.add(new ExecuteDslScripts(SCRIPT))
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
        job.buildersList.add(new ExecuteDslScripts(targets: 'jobs.groovy'))
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
        job.buildersList.add(new ExecuteDslScripts(targets: 'jobs.groovy'))
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
        job.buildersList.add(new ExecuteDslScripts(targets: '**/*.groovy'))
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
        job.buildersList.add(new ExecuteDslScripts(targets: 'jobs.groovy'))
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
        job.buildersList.add(new ExecuteDslScripts(targets: 'mydsl/jobs.groovy'))
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedJobAction = RemovedJobAction.DELETE
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedJobAction = RemovedJobAction.DELETE
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedJobAction = RemovedJobAction.DELETE
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('/folder/test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("/folder/different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedJobAction = RemovedJobAction.DELETE
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedJobAction = RemovedJobAction.DELETE
        builder1.lookupStrategy = LookupStrategy.SEED_JOB
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('/folder/test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedJobAction = RemovedJobAction.DELETE
        builder2.lookupStrategy = LookupStrategy.SEED_JOB
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedJobAction = RemovedJobAction.DELETE
        runBuild(job, builder1)

        then:
        assertTrue(jenkinsRule.jenkins.getItemByFullName('test-job') instanceof FreeStyleProject)

        when:
        String script2 = 'job("different-job")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedJobAction = RemovedJobAction.DELETE
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
        ExecuteDslScripts builder = new ExecuteDslScripts(script)
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
        ExecuteDslScripts builder = new ExecuteDslScripts(script)
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
        ExecuteDslScripts builder = new ExecuteDslScripts(script)
        builder.removedJobAction = RemovedJobAction.DELETE
        builder.lookupStrategy = LookupStrategy.SEED_JOB
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
        runBuild(seedJob, new ExecuteDslScripts('// do nothing'))

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
        ExecuteDslScripts builder = new ExecuteDslScripts('// do nothing')
        builder.removedJobAction = RemovedJobAction.DISABLE
        runBuild(seedJob, builder)

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
        ExecuteDslScripts builder = new ExecuteDslScripts('// do nothing')
        builder.removedJobAction = RemovedJobAction.DELETE
        runBuild(seedJob, builder)

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
        ExecuteDslScripts builder = new ExecuteDslScripts(VIEW_SCRIPT)
        builder.ignoreExisting = true
        job.buildersList.add(builder)
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedViewAction = RemovedViewAction.DELETE
        runBuild(job, builder1)

        then:
        jenkinsRule.instance.getView('test-view') instanceof ListView

        when:
        String script2 = 'listView("different-view")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedViewAction = RemovedViewAction.DELETE
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedViewAction = RemovedViewAction.DELETE
        runBuild(job, builder1)

        then:
        folder.getView('test-view') instanceof ListView

        when:
        String script2 = 'listView("/folder/different-view")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedViewAction = RemovedViewAction.DELETE
        runBuild(job, builder2)

        then:
        folder.getView('different-view') instanceof ListView
        folder.getView('test-view') == null
    }

    def 'delete view in folder after folder has been deleted'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')

        when:
        String script1 = 'folder("folder")\nlistView("/folder/test-view")'
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedJobAction = RemovedJobAction.DELETE
        builder1.removedViewAction = RemovedViewAction.DELETE
        runBuild(job, builder1)

        then:
        Folder folder = jenkinsRule.instance.getItemByFullName('folder', Folder)
        folder != null
        folder.getView('test-view') instanceof ListView

        when:
        String script2 = '// no-op'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedJobAction = RemovedJobAction.DELETE
        builder2.removedViewAction = RemovedViewAction.DELETE
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
        ExecuteDslScripts builder1 = new ExecuteDslScripts(script1)
        builder1.removedViewAction = RemovedViewAction.DELETE
        builder1.lookupStrategy = LookupStrategy.SEED_JOB
        runBuild(job, builder1)

        then:
        folder.getView('test-view') instanceof ListView

        when:
        String script2 = 'listView("different-view")'
        ExecuteDslScripts builder2 = new ExecuteDslScripts(script2)
        builder2.removedViewAction = RemovedViewAction.DELETE
        builder2.lookupStrategy = LookupStrategy.SEED_JOB
        runBuild(job, builder2)

        then:
        folder.getView('different-view') instanceof ListView
        folder.getView('test-view') == null
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
        ExecuteDslScripts builder = new ExecuteDslScripts(FOLDER_SCRIPT)
        builder.ignoreExisting = true
        job.buildersList.add(builder)
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
        job.buildersList.add(new ExecuteDslScripts(FOLDER_SCRIPT))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS

        when:
        job.buildersList.clear()
        ExecuteDslScripts builder = new ExecuteDslScripts('// empty')
        builder.removedJobAction = RemovedJobAction.DELETE
        job.buildersList.add(builder)
        freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-folder') == null
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
        job.buildersList.add(new ExecuteDslScripts(emptyArchiveScript))

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.instance.getItem('test-job') instanceof FreeStyleProject
    }

    def 'extension is used'() {
        setup:
        FreeStyleProject seedJob = jenkinsRule.createFreeStyleProject('seed')
        seedJob.buildersList.add(new ExecuteDslScripts(EXTENSION_SCRIPT))

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

    def 'deprecation warning in DSL script with unstableOnDeprecation set to #{unstableOnDeprecation}'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                scriptText: this.class.getResourceAsStream('deprecation.groovy').text,
                unstableOnDeprecation: unstableOnDeprecation
        ))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild build = job.scheduleBuild2(0).get()

        then:
        build.getLog(25).join('\n') =~
                /Warning: \(script, line 3\) support for Exclusion Plug-in versions older than 0.12 is deprecated/
        build.result == result

        where:
        unstableOnDeprecation || result
        true                  || Result.UNSTABLE
        false                 || Result.SUCCESS
    }

    def 'unstable or failure on missing plugin'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                scriptText: this.class.getResourceAsStream('missingPlugin.groovy').text,
                failOnMissingPlugin: failOnMissingPlugin
        ))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild build = job.scheduleBuild2(0).get()

        then:
        build.getLog(25).join('\n') =~
                /${message}: \(script, line 3\) version .+ or later of plugin 'email-ext' needs to be installed/
        build.result == result

        where:
        failOnMissingPlugin || result   | message
        true                || FAILURE  | 'ERROR'
        false               || UNSTABLE | 'Warning'
    }

    def 'JENKINS-32995'() {
        setup:
        jenkinsRule.instance.createProject(Folder, 'Foo')
        Folder folder = jenkinsRule.instance.createProject(Folder, 'Bar')
        FreeStyleProject job = folder.createProject(FreeStyleProject, 'seed')
        ExecuteDslScripts builder = new ExecuteDslScripts('job("Foo")')
        builder.removedJobAction = RemovedJobAction.DISABLE
        builder.lookupStrategy = LookupStrategy.SEED_JOB
        job.buildersList.add(builder)
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
    }

    def 'JENKINS-32628 script name which collides with package name'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.scheduleBuild2(0).get() // run a build to create a workspace
        job.someWorkspace.child('jenkins.dsl').write('job("test")', 'UTF-8')
        job.buildersList.add(new ExecuteDslScripts(targets: 'jenkins.dsl'))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        freeStyleBuild.getLog(25).join('\n') =~ /identical to a package name/
        freeStyleBuild.getLog(25).join('\n') =~ /jenkins.dsl/
    }

    def 'JENKINS-39153 GString arguments in auto-generated DSL'() {
        setup:
        Project job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(scriptText: this.class.getResourceAsStream('gstring.groovy').text))
        job.onCreatedFromScratch()

        when:
        Run build = job.scheduleBuild2(0).get()

        then:
        build.result == SUCCESS
    }

    def 'ignore missing file'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(targets: 'jenkins.dsl', ignoreMissingFiles: true))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
    }

    def 'ignore empty wildcard'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(targets: '*.dsl', ignoreMissingFiles: true))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
    }

    def 'classpath per script'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.scheduleBuild2(0).get() // run a build to create a workspace
        job.someWorkspace.child('projectA/Utils.groovy').write('class Utils { static NAME = "projectA" }', 'UTF-8')
        job.someWorkspace.child('projectA/script.groovy').write('job(Utils.NAME)', 'UTF-8')
        job.someWorkspace.child('projectB/Utils.groovy').write('class Utils { static NAME = "projectB" }', 'UTF-8')
        job.someWorkspace.child('projectB/script.groovy').write('job(Utils.NAME)', 'UTF-8')
        job.buildersList.add(new ExecuteDslScripts(targets: 'projectA/script.groovy\nprojectB/script.groovy'))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        jenkinsRule.jenkins.getItem('projectA') != null
        jenkinsRule.jenkins.getItem('projectB') != null
    }

    def 'JENKINS-39137'() {
        setup:
        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        folder.createProject(Folder, 'nested')
        Project job = folder.createProject(FreeStyleProject, 'seed')
        job.buildersList.add(new ExecuteDslScripts(
                scriptText: 'job("folder/nested/foo")',
                lookupStrategy: LookupStrategy.SEED_JOB
        ))
        job.onCreatedFromScratch()

        when:
        Run build = job.scheduleBuild2(0).get()

        then:
        build.result == FAILURE
    }

    def 'creates config files'() {
        setup:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(scriptText: getClass().getResource('configFiles.groovy').text))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        GlobalConfigFiles.get().getById('one') instanceof CustomConfig
        GlobalConfigFiles.get().getById('one').name == 'Config 1'
        GlobalConfigFiles.get().getById('one').comment == 'lorem'
        GlobalConfigFiles.get().getById('one').content == 'ipsum'
        GlobalConfigFiles.get().getById('one').providerId == '???'
        GlobalConfigFiles.get().getById('two') instanceof PowerShellConfig
        GlobalConfigFiles.get().getById('two').name == 'Config 2'
        GlobalConfigFiles.get().getById('two').comment == 'foo'
        GlobalConfigFiles.get().getById('two').content == 'bar'
    }

    def 'creates config files ignore existing'() {
        setup:
        GlobalConfigFiles.get().save(new CustomConfig('one', '111', '222', '333', '444'))
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('seed')
        job.buildersList.add(new ExecuteDslScripts(
                scriptText: getClass().getResource('configFiles.groovy').text,
                ignoreExisting: true
        ))
        job.onCreatedFromScratch()

        when:
        FreeStyleBuild freeStyleBuild = job.scheduleBuild2(0).get()

        then:
        freeStyleBuild.result == SUCCESS
        GlobalConfigFiles.get().getById('one') instanceof CustomConfig
        GlobalConfigFiles.get().getById('one').name == '111'
        GlobalConfigFiles.get().getById('one').comment == '222'
        GlobalConfigFiles.get().getById('one').content == '333'
        GlobalConfigFiles.get().getById('one').providerId == '444'
        GlobalConfigFiles.get().getById('two') instanceof PowerShellConfig
        GlobalConfigFiles.get().getById('two').name == 'Config 2'
        GlobalConfigFiles.get().getById('two').comment == 'foo'
        GlobalConfigFiles.get().getById('two').content == 'bar'
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
