package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.FilePath
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.FreeStyleProject
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

class WorkspaceProtocolSpec extends Specification {
    @Shared
    @ClassRule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @Shared
    AbstractProject project

    @Shared
    FilePath fileInProject

    @Shared
    FilePath directoryInProject

    @Shared
    AbstractProject projectInFolder

    @Shared
    FilePath fileInProjectInFolder

    @Shared
    FilePath directoryInProjectInFolder

    def setupSpec() {
        project = jenkinsRule.createFreeStyleProject('test-project')
        AbstractBuild build = project.scheduleBuild2(0).get()
        fileInProject = build.workspace.child('file')
        directoryInProject = build.workspace.child('dir')
        directoryInProject.mkdirs()

        Folder folder = jenkinsRule.jenkins.createProject(Folder, 'folder')
        projectInFolder = folder.createProject(FreeStyleProject, 'test-project')
        build = projectInFolder.scheduleBuild2(0).get()
        fileInProjectInFolder = build.workspace.child('file')
        directoryInProjectInFolder = build.workspace.child('dir')
        directoryInProjectInFolder.mkdirs()
    }

    def 'url for project'() {
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
        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(projectInFolder)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)

        then:
        projectInFolder == returnedProject
    }

    def 'url for project with file'() {
        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(project.lastBuild, fileInProject)

        then:
        url.host == 'test-project'
        url.file == '/file'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        fileInProject == returnedFilePath
    }

    def 'url for project in folder with file'() {
        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(projectInFolder.lastBuild, fileInProjectInFolder)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/file'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        projectInFolder == returnedProject
        fileInProjectInFolder == returnedFilePath
    }

    def 'url for project with directory'() {
        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(project.lastBuild, directoryInProject)

        then:
        url.host == 'test-project'
        url.file == '/dir/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        project == returnedProject
        directoryInProject == returnedFilePath
    }

    def 'url for project in folder with directory'() {
        when:
        URL url = WorkspaceProtocol.createWorkspaceUrl(projectInFolder.lastBuild, directoryInProjectInFolder)

        then:
        url.host == 'folder%2Ftest-project'
        url.file == '/dir/'

        when:
        AbstractProject returnedProject = WorkspaceProtocol.getProjectFromWorkspaceUrl(url)
        FilePath returnedFilePath = WorkspaceProtocol.getFilePathFromUrl(url)

        then:
        projectInFolder == returnedProject
        directoryInProjectInFolder == returnedFilePath
    }
}
