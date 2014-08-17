package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.views.BuildPipelineView
import javaposse.jobdsl.dsl.views.ListView
import spock.lang.Specification

class JobParentSpec extends Specification {
    JobParent parent = Spy(JobParent)
    JobManagement jobManagement = Mock(JobManagement)

    def setup() {
        parent.jm = jobManagement
    }

    def 'default view type'() {
        when:
        View view = parent.view {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'list view'() {
        when:
        View view = parent.view(type: ViewType.ListView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof ListView
        parent.referencedViews.contains(view)
    }

    def 'build pipeline view'() {
        when:
        View view = parent.view(type: ViewType.BuildPipelineView) {
            name 'test'
        }

        then:
        view.name == 'test'
        view instanceof BuildPipelineView
        parent.referencedViews.contains(view)
    }

    def 'folder'() {
        when:
        Folder folder = parent.folder {
            name 'test'
        }

        then:
        folder.name == 'test'
        parent.referencedJobs.contains(folder)
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
}
