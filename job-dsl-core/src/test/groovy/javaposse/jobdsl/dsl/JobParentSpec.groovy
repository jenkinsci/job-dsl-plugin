package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.BuildMonitorView
import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.DeliveryPipelineView
import javaposse.jobdsl.dsl.views.ListView
import javaposse.jobdsl.dsl.views.NestedView
import javaposse.jobdsl.dsl.views.SectionedView
import spock.lang.Specification

class JobParentSpec extends Specification {
    JobParent parent = Spy(JobParent)
    JobManagement jobManagement = Mock(JobManagement)

    def setup() {
        parent.jm = jobManagement
    }

    def 'default view type deprecated variant'() {
        when:
        View view = parent.view {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'default view type'() {
        when:
        View view = parent.view('test') {
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'list view deprecated variant'() {
        when:
        View view = parent.view(type: ViewType.ListView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'list view'() {
        when:
        View view = parent.view('test', type: ViewType.ListView) {
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'build pipeline view deprecated variant'() {
        when:
        View view = parent.view(type: ViewType.BuildPipelineView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof BuildPipelineView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'build pipeline view'() {
        when:
        View view = parent.view('test', type: ViewType.BuildPipelineView) {
        }

        then:
        view.name == 'test'
        view instanceof BuildPipelineView
        parent.referencedViews.contains(view)
    }

    def 'build monitor view deprecated variant'() {
        when:
        View view = parent.view(type: ViewType.BuildMonitorView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof BuildMonitorView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'build monitor view'() {
        when:
        View view = parent.view('test', type: ViewType.BuildMonitorView) {
        }

        then:
        view.name == 'test'
        view instanceof BuildMonitorView
        parent.referencedViews.contains(view)
    }

    def 'sectioned view deprecated variant'() {
        when:
        View view = parent.view(type: ViewType.SectionedView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof SectionedView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'sectioned view'() {
        when:
        View view = parent.view('test', type: ViewType.SectionedView) {
        }

        then:
        view.name == 'test'
        view instanceof SectionedView
        parent.referencedViews.contains(view)
    }

    def 'nested view deprecated variant'() {
        when:
        View view = parent.view(type: ViewType.NestedView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof NestedView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'nested view'() {
        when:
        View view = parent.view('test', type: ViewType.NestedView) {
        }

        then:
        view.name == 'test'
        view instanceof NestedView
        parent.referencedViews.contains(view)
    }

    def 'delivery pipeline view deprecated variant'() {
        when:
        View view = parent.view(type: ViewType.DeliveryPipelineView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof DeliveryPipelineView
        parent.referencedViews.contains(view)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'delivery pipeline view'() {
        when:
        View view = parent.view('test', type: ViewType.DeliveryPipelineView) {
        }

        then:
        view.name == 'test'
        view instanceof DeliveryPipelineView
        parent.referencedViews.contains(view)
    }

    def 'folder deprecated variant'() {
        when:
        Folder folder = parent.folder {
            name 'test'
        }

        then:
        folder.name == 'test'
        parent.referencedJobs.contains(folder)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'folder'() {
        when:
        Folder folder = parent.folder('test') {
        }

        then:
        folder.name == 'test'
        parent.referencedJobs.contains(folder)
    }

    def 'default config file deprecated variant'() {
        when:
        ConfigFile configFile = parent.configFile {
            name 'test'
        }

        then:
        configFile.name == 'test'
        configFile.type == ConfigFileType.Custom
        parent.referencedConfigFiles.contains(configFile)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'default config file'() {
        when:
        ConfigFile configFile = parent.configFile('test') {
        }

        then:
        configFile.name == 'test'
        configFile.type == ConfigFileType.Custom
        parent.referencedConfigFiles.contains(configFile)
    }

    def 'custom config file deprecated variant'() {
        when:
        ConfigFile configFile = parent.configFile(type: ConfigFileType.Custom) {
            name 'test'
        }

        then:
        configFile.name == 'test'
        configFile.type == ConfigFileType.Custom
        parent.referencedConfigFiles.contains(configFile)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'custom config file'() {
        when:
        ConfigFile configFile = parent.configFile('test', type: ConfigFileType.Custom) {
        }

        then:
        configFile.name == 'test'
        configFile.type == ConfigFileType.Custom
        parent.referencedConfigFiles.contains(configFile)
    }

    def 'Maven settings config file deprecated variant'() {
        when:
        ConfigFile configFile = parent.configFile(type: ConfigFileType.MavenSettings) {
            name 'test'
        }

        then:
        configFile.name == 'test'
        configFile.type == ConfigFileType.MavenSettings
        parent.referencedConfigFiles.contains(configFile)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'Maven settings config file'() {
        when:
        ConfigFile configFile = parent.configFile('test', type: ConfigFileType.MavenSettings) {
        }

        then:
        configFile.name == 'test'
        configFile.type == ConfigFileType.MavenSettings
        parent.referencedConfigFiles.contains(configFile)
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
        thrown(IllegalArgumentException)

        when:
        parent.readFileFromWorkspace('')

        then:
        thrown(IllegalArgumentException)
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
        thrown(IllegalArgumentException)

        when:
        parent.readFileFromWorkspace('')

        then:
        thrown(IllegalArgumentException)
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
        thrown(IllegalArgumentException)

        when:
        parent.readFileFromWorkspace('my-job', '')

        then:
        thrown(IllegalArgumentException)

        when:
        parent.readFileFromWorkspace(null, 'foo.txt')

        then:
        thrown(IllegalArgumentException)

        when:
        parent.readFileFromWorkspace('', 'foo.txt')

        then:
        thrown(IllegalArgumentException)
    }

    def 'workflow deprecated variant'() {
        when:
        Job job = parent.job(type: JobType.Workflow) {
            name 'test'
        }

        then:
        job.name == 'test'
        job.type == JobType.Workflow
        parent.referencedJobs.contains(job)
        2 * jobManagement.logDeprecationWarning()
    }

    def 'workflow'() {
        when:
        Job job = parent.job('test', type: JobType.Workflow) {
        }

        then:
        job.name == 'test'
        job.type == JobType.Workflow
        parent.referencedJobs.contains(job)
    }
}
