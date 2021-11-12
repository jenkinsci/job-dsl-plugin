package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.FreeStyleJob
import javaposse.jobdsl.dsl.jobs.IvyJob
import javaposse.jobdsl.dsl.jobs.MatrixJob
import javaposse.jobdsl.dsl.jobs.MavenJob
import javaposse.jobdsl.dsl.jobs.MultiJob
import javaposse.jobdsl.dsl.jobs.OrganizationFolderJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob
import javaposse.jobdsl.dsl.jobs.MultibranchWorkflowJob
import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.CategorizedJobsView
import javaposse.jobdsl.dsl.views.DashboardView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.PipelineAggregatorView
import javaposse.jobdsl.dsl.views.SectionedView
import spock.lang.Specification

class JobParentSpec extends Specification {
    private final JobParent parent = Spy(JobParent)
    private final JobManagement jobManagement = Mock(JobManagement)

    def setup() {
        parent.jm = jobManagement
    }

    def 'list view'() {
        when:
        View view = parent.listView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
    }

    def 'list view without closure'() {
        when:
        View view = parent.listView('test')

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'build pipeline view'() {
        when:
        View view = parent.buildPipelineView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof BuildPipelineView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requirePlugin('build-pipeline-plugin', true)
    }

    def 'build pipeline view without closure'() {
        when:
        View view = parent.buildPipelineView('test')

        then:
        view.name == 'test'
        view instanceof BuildPipelineView
        parent.referencedViews.contains(view)
        1 * jobManagement.requirePlugin('build-pipeline-plugin', true)
    }

    def 'build monitor view'() {
        when:
        View view = parent.buildMonitorView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof BuildMonitorView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requirePlugin('build-monitor-plugin', true)
    }

    def 'build monitor view without closure'() {
        when:
        View view = parent.buildMonitorView('test')

        then:
        view.name == 'test'
        view instanceof BuildMonitorView
        parent.referencedViews.contains(view)
        1 * jobManagement.requirePlugin('build-monitor-plugin', true)
    }

    def 'sectioned view'() {
        when:
        View view = parent.sectionedView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof SectionedView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requirePlugin('sectioned-view', true)
    }

    def 'sectioned view without closure'() {
        when:
        View view = parent.sectionedView('test')

        then:
        view.name == 'test'
        view instanceof SectionedView
        parent.referencedViews.contains(view)
        1 * jobManagement.requirePlugin('sectioned-view', true)
    }

    def 'nested view'() {
        when:
        View view = parent.nestedView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof NestedView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requirePlugin('nested-view', true)
    }

    def 'nested view without closure'() {
        when:
        View view = parent.nestedView('test')

        then:
        view.name == 'test'
        view instanceof NestedView
        parent.referencedViews.contains(view)
        1 * jobManagement.requirePlugin('nested-view', true)
    }

    def 'delivery pipeline view'() {
        when:
        View view = parent.deliveryPipelineView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof DeliveryPipelineView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0', true)
    }

    def 'delivery pipeline view without closure'() {
        when:
        View view = parent.deliveryPipelineView('test')

        then:
        view.name == 'test'
        view instanceof DeliveryPipelineView
        parent.referencedViews.contains(view)
        1 * jobManagement.requireMinimumPluginVersion('delivery-pipeline-plugin', '0.10.0', true)
    }

    def 'should add categorized jobs view'() {
        when:
        View view = parent.categorizedJobsView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof CategorizedJobsView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '1.8', true)
    }

    def 'should add categorized jobs view without closure'() {
        when:
        View view = parent.categorizedJobsView('test')

        then:
        view.name == 'test'
        view instanceof CategorizedJobsView
        parent.referencedViews.contains(view)
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '1.8', true)
    }

    def 'should add dashboard view'() {
        when:
        View view = parent.dashboardView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof DashboardView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requireMinimumPluginVersion('dashboard-view', '2.9.7', true)
    }

    def 'should add dashboard view without closure'() {
        when:
        View view = parent.dashboardView('test')

        then:
        view.name == 'test'
        view instanceof DashboardView
        parent.referencedViews.contains(view)
        1 * jobManagement.requireMinimumPluginVersion('dashboard-view', '2.9.7', true)
    }

    def 'folder'() {
        when:
        Folder folder = parent.folder('test') {
            displayName('foo')
        }

        then:
        folder.name == 'test'
        parent.referencedJobs.contains(folder)
        folder.node.displayName[0].text() == 'foo'
        1 * jobManagement.requireMinimumPluginVersion('cloudbees-folder', '5.14', true)
    }

    def 'folder without closure'() {
        when:
        Folder folder = parent.folder('test')

        then:
        folder.name == 'test'
        parent.referencedJobs.contains(folder)
        1 * jobManagement.requireMinimumPluginVersion('cloudbees-folder', '5.14', true)
    }

    def 'pipeline aggregator view'() {
        when:
        View view = parent.pipelineAggregatorView('test') {
            description('foo')
        }

        then:
        view.name == 'test'
        view instanceof PipelineAggregatorView
        parent.referencedViews.contains(view)
        view.node.description[0].text() == 'foo'
        1 * jobManagement.requireMinimumPluginVersion('pipeline-aggregator-view', '1.8', true)
    }

    def 'pipeline aggregator view without closure'() {
        when:
        View view = parent.pipelineAggregatorView('test')

        then:
        view.name == 'test'
        view instanceof PipelineAggregatorView
        parent.referencedViews.contains(view)
        1 * jobManagement.requireMinimumPluginVersion('pipeline-aggregator-view', '1.8', true)
    }

    def 'readFileInWorkspace from seed job'() {
        jobManagement.readFileInWorkspace('foo.txt') >> 'hello'

        when:
        String result = parent.readFileFromWorkspace('foo.txt')

        then:
        result == 'hello'

        when:
        parent.readFileFromWorkspace(null)

        then:
        thrown(DslScriptException)

        when:
        parent.readFileFromWorkspace('')

        then:
        thrown(DslScriptException)
    }

    def 'streamFileFromWorkspace'() {
        InputStream inputStream = Mock(InputStream)
        jobManagement.streamFileInWorkspace('foo.txt') >> inputStream

        when:
        InputStream result = parent.streamFileFromWorkspace('foo.txt')

        then:
        result == inputStream

        when:
        parent.readFileFromWorkspace(null)

        then:
        thrown(DslScriptException)

        when:
        parent.readFileFromWorkspace('')

        then:
        thrown(DslScriptException)
    }

    def 'readFileInWorkspace from other job'() {
        jobManagement.readFileInWorkspace('my-job', 'foo.txt') >> 'hello'

        when:
        String result = parent.readFileFromWorkspace('my-job', 'foo.txt')

        then:
        result == 'hello'

        when:
        parent.readFileFromWorkspace('my-job', null)

        then:
        thrown(DslScriptException)

        when:
        parent.readFileFromWorkspace('my-job', '')

        then:
        thrown(DslScriptException)

        when:
        parent.readFileFromWorkspace(null, 'foo.txt')

        then:
        thrown(DslScriptException)

        when:
        parent.readFileFromWorkspace('', 'foo.txt')

        then:
        thrown(DslScriptException)
    }

    def 'job is an alias for freeStyleJob'() {
        when:
        FreeStyleJob job = parent.job('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
    }

    def 'freeStyleJob'() {
        when:
        FreeStyleJob job = parent.freeStyleJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
    }

    def 'ivyJob'() {
        when:
        IvyJob job = parent.ivyJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('ivy', '1.23', true)
    }

    def 'ivyJob without closure'() {
        when:
        IvyJob job = parent.ivyJob('test')

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('ivy', '1.23', true)
    }

    def 'matrixJob'() {
        when:
        MatrixJob job = parent.matrixJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requirePlugin('matrix-project', true)
    }

    def 'mavenJob'() {
        when:
        MavenJob job = parent.mavenJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('maven-plugin', '2.3', true)
    }

    def 'multiJob'() {
        when:
        MultiJob job = parent.multiJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('jenkins-multijob-plugin', '1.22', true)
    }

    def 'pipeline'() {
        when:
        WorkflowJob job = parent.pipelineJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('workflow-job', '2.4', true)
    }

    def 'multibranchPipelineJob'() {
        when:
        MultibranchWorkflowJob job = parent.multibranchPipelineJob('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('workflow-multibranch', '2.10', true)
    }

    def 'organization folder job'() {
        when:
        OrganizationFolderJob job = parent.organizationFolder('test') {
        }

        then:
        job.name == 'test'
        parent.referencedJobs.contains(job)
        1 * jobManagement.requireMinimumPluginVersion('branch-api', '2.0.0', true)
    }
}
