package javaposse.jobdsl.plugin

import com.google.common.collect.Sets
import hudson.EnvVars
import hudson.FilePath
import hudson.model.AbstractBuild
import javaposse.jobdsl.dsl.ScriptRequest

class ScriptRequestGenerator {

    final AbstractBuild build
    EnvVars env

    ScriptRequestGenerator(AbstractBuild build, EnvVars env) {
        this.build = build
        this.env = env
    }

    public Set<ScriptRequest> getScriptRequests(String targets, boolean usingScriptText, String scriptText, boolean ignoreExisting) throws IOException, InterruptedException {
        Set<ScriptRequest> scriptRequests = Sets.newHashSet();

        if(usingScriptText) {
            URL workspaceUrl = WorkspaceProtocol.createWorkspaceUrl(build.project)
            ScriptRequest request = new ScriptRequest(null, scriptText, workspaceUrl, ignoreExisting);
            scriptRequests.add(request);
        } else {
            String targetsStr = env.expand(targets);

            FilePath[] filePaths =  build.getWorkspace().list(targetsStr.replace("\n", ","));
            for (FilePath filePath : filePaths) {
                URL relativeWorkspaceUrl = WorkspaceProtocol.createWorkspaceUrl(build, filePath.parent)
                ScriptRequest request = new ScriptRequest(filePath.name, null, relativeWorkspaceUrl, ignoreExisting);
                scriptRequests.add(request);
            }
        }
        return scriptRequests;
    }
}
