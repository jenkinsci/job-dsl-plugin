package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.FilePath
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.FreeStyleProject
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class WorkspaceProtocolSpec extends Specification {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    def 'url for project'() {
        given:
        AbstractProject project = jenkinsRule.createFreeStyleProject('test-project')

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(project)

        then:
        url.host == 'test-project'
        url.file == '/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)

        then:
        project == returnedProject
    }

    def 'url for project in folder'() {
        given:
        Folder folder = jenkinsRule.jenkins.createProject Folder, 'folder'
        AbstractProject project = folder.createProject FreeStyleProject, 'test-project'

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(project)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)

        then:
        project == returnedProject
    }

    def 'url for project with file'() {
        given:
        AbstractProject project = jenkinsRule.createFreeStyleProject('test-project')
        AbstractBuild build = project.scheduleBuild2(0).get()
        FilePath filePath = build.workspace.child('file')

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(build, filePath)

        then:
        url.host == 'test-project'
        url.file == '/file'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        filePath == returnedFilePath
    }

    def 'url for project in folder with file'() {
        given:
        Folder folder = jenkinsRule.jenkins.createProject Folder, 'folder'
        AbstractProject project = folder.createProject FreeStyleProject, 'test-project'
        AbstractBuild build = project.scheduleBuild2(0).get()
        FilePath filePath = build.workspace.child('file')

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(build, filePath)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/file'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        filePath == returnedFilePath
    }

    def 'url for project with directory'() {
        given:
        AbstractProject project = jenkinsRule.createFreeStyleProject('test-project')
        AbstractBuild build = project.scheduleBuild2(0).get()
        FilePath filePath = build.workspace.child('dir')
        filePath.mkdirs()

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(build, filePath)

        then:
        url.host == 'test-project'
        url.file == '/dir/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        filePath == returnedFilePath
    }

    def 'url for project in folder with directory'() {
        given:
        Folder folder = jenkinsRule.jenkins.createProject Folder, 'folder'
        AbstractProject project = folder.createProject FreeStyleProject, 'test-project'
        AbstractBuild build = project.scheduleBuild2(0).get()
        FilePath filePath = build.workspace.child('dir')
        filePath.mkdirs()

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(build, filePath)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/dir/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        filePath == returnedFilePath
    }
}
