package javaposse.jobdsl.plugin

import hudson.FilePath
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import jenkins.model.Jenkins

class WorkspaceProtocol {

    /**
     * Create a workspace URL that represents the base dir of the given AbstractProject.
     */
    static URL createWorkspaceUrl(AbstractProject project) {
        String jobName = project.fullName
        String encodedJobName = URLEncoder.encode(jobName, "UTF-8")
        new URL(null, "workspace://$encodedJobName/", new WorkspaceUrlHandler())
    }

    /**
     * Create a workspace URL that represents the given FilePath.
     */
    static URL createWorkspaceUrl(AbstractBuild build, FilePath filePath) {
        String relativePath = filePath.getRemote() - build.workspace.getRemote()
        relativePath = relativePath.replaceAll('\\\\', '/') // normalize for Windows
        new URL(createWorkspaceUrl(build.project), "$relativePath/", new WorkspaceUrlHandler())
    }

    /**
     * Parse the AbstractProject from the given workspace URL representation.
     */
    static AbstractProject getProjectFromWorkspaceUrl(URL url) {
        Jenkins jenkins = Jenkins.instance
        if (!jenkins) {
            throw new IllegalStateException("Not in a running Jenkins")
        }

        String jobName = url.host
        String decodedJobName = URLDecoder.decode(jobName, "UTF-8")
        (AbstractProject) Jenkins.instance.getItemByFullName(decodedJobName)
    }

    /**
     * Parse the FilePath from the given workspace URL representation.
     */
    static FilePath getFilePathFromUrl(URL url) {
        AbstractProject project = getProjectFromWorkspaceUrl(url)
        FilePath workspace = project.someWorkspace
        String relativePath = url.file[1..-1] // remove leading slash
        workspace.child relativePath
    }
}
