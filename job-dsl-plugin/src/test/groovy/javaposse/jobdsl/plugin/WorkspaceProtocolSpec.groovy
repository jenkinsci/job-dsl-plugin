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
        AbstractBuild build = project.createExecutable()
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(project)
        build.setWorkspace workspace
        FilePath filePath = workspace.child('files')

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(build, filePath)

        then:
        url.host == 'test-project'
        url.file == '/files/'

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
        AbstractBuild build = project.createExecutable()
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(project)
        build.setWorkspace workspace
        FilePath filePath = workspace.child('files')

        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(build, filePath)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/files/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        filePath == returnedFilePath
    }
}
