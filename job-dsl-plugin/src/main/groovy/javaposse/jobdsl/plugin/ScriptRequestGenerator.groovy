package javaposse.jobdsl.plugin

import com.google.common.collect.Sets
import hudson.EnvVars
import hudson.FilePath
import hudson.model.AbstractBuild
import javaposse.jobdsl.dsl.ScriptRequest

import static javaposse.jobdsl.plugin.WorkspaceProtocol.createWorkspaceUrl

class ScriptRequestGenerator {

    final AbstractBuild build
    EnvVars env

    ScriptRequestGenerator(AbstractBuild build, EnvVars env) {
        this.build = build
        this.env = env
    }

    Set<ScriptRequest> getScriptRequests(String targets, boolean usingScriptText, String scriptText,
                                         boolean ignoreExisting,
                                         String additionalClasspath) throws IOException, InterruptedException {
        Set<ScriptRequest> scriptRequests = Sets.newLinkedHashSet()

        List<URL> classpath = []
        if (additionalClasspath) {
            String expandedClasspath = env.expand(additionalClasspath)
            expandedClasspath.split('\n').each { classpath << createWorkspaceUrl(build, build.workspace.child(it)) }
        }
        if (usingScriptText) {
            URL[] urlRoots = ([createWorkspaceUrl(build.project)] + classpath) as URL[]
            ScriptRequest request = new ScriptRequest(null, scriptText, urlRoots, ignoreExisting)
            scriptRequests.add(request)
        } else {
            String targetsStr = env.expand(targets)

            FilePath[] filePaths =  build.workspace.list(targetsStr.replace('\n', ','))
            for (FilePath filePath : filePaths) {
                URL[] urlRoots = ([createWorkspaceUrl(build, filePath.parent)] + classpath) as URL[]
                ScriptRequest request = new ScriptRequest(filePath.name, null, urlRoots, ignoreExisting)
                scriptRequests.add(request)
            }
        }
        scriptRequests
    }
}
