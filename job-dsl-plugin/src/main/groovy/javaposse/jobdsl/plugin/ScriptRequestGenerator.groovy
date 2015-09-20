package javaposse.jobdsl.plugin

import hudson.EnvVars
import hudson.FilePath
import hudson.model.AbstractBuild
import javaposse.jobdsl.dsl.ScriptRequest

import static javaposse.jobdsl.plugin.WorkspaceProtocol.createWorkspaceUrl

class ScriptRequestGenerator implements Closeable {
    final AbstractBuild build
    EnvVars env
    final Map<FilePath, File> cachedFiles = [:]

    ScriptRequestGenerator(AbstractBuild build, EnvVars env) {
        this.build = build
        this.env = env
    }

    Set<ScriptRequest> getScriptRequests(String targets, boolean usingScriptText, String scriptText,
                                         boolean ignoreExisting,
                                         String additionalClasspath) throws IOException, InterruptedException {
        Set<ScriptRequest> scriptRequests = new LinkedHashSet<ScriptRequest>()

        List<URL> classpath = []
        if (additionalClasspath) {
            String expandedClasspath = env.expand(additionalClasspath)
            expandedClasspath.split('\n').each {
                if (it.contains('*') || it.contains('?')) {
                    classpath.addAll(build.workspace.list(it).collect { createClasspathURL(it) })
                } else {
                    classpath << createClasspathURL(build.workspace.child(it))
                }
            }
        }
        if (usingScriptText) {
            URL[] urlRoots = ([createWorkspaceUrl(build.project)] + classpath) as URL[]
            ScriptRequest request = new ScriptRequest(null, scriptText, urlRoots, ignoreExisting)
            scriptRequests.add(request)
        } else {
            String targetsStr = env.expand(targets)

            FilePath[] filePaths = build.workspace.list(targetsStr.replace('\n', ','))
            for (FilePath filePath : filePaths) {
                URL[] urlRoots = ([createWorkspaceUrl(build, filePath.parent)] + classpath) as URL[]
                ScriptRequest request = new ScriptRequest(filePath.name, null, urlRoots, ignoreExisting)
                scriptRequests.add(request)
            }
        }
        scriptRequests
    }

    @Override
    void close() throws IOException {
        cachedFiles.values().each {
            it.delete()
        }
    }

    private URL createClasspathURL(FilePath filePath) {
        if (filePath.isRemote()) {
            if (filePath.directory) {
                createWorkspaceUrl(build, filePath)
            } else {
                File file = cachedFiles[filePath]
                if (!file) {
                    file = copyToLocal(filePath)
                    cachedFiles.put(filePath, file)
                }
                file.toURI().toURL()
            }
        } else {
            filePath.toURI().toURL()
        }
    }

    private static File copyToLocal(FilePath filePath) {
        File file = File.createTempFile('jobdsl', '.jar')
        filePath.copyTo(new FilePath(file))
        file
    }
}
