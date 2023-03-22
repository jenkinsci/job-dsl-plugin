package javaposse.jobdsl.plugin

import groovy.transform.PackageScope
import hudson.EnvVars
import hudson.FilePath
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.ScriptRequest
import org.apache.commons.io.FilenameUtils

class ScriptRequestGenerator implements Closeable {
    final FilePath workspace
    final EnvVars env
    final Map<FilePath, File> cachedFiles = [:]

    ScriptRequestGenerator(FilePath workspace, EnvVars env) {
        this.workspace = workspace
        this.env = env
    }

    Set<ScriptRequest> getScriptRequests(String targets, boolean usingScriptText, String scriptText,
                                         boolean ignoreExisting, boolean ignoreMissingFiles = false,
                                         String additionalClasspath) throws IOException, InterruptedException {
        Set<ScriptRequest> scriptRequests = new LinkedHashSet<ScriptRequest>()

        List<URL> classpath = []
        if (additionalClasspath) {
            String expandedClasspath = env.expand(additionalClasspath)
            expandedClasspath.split('\n').each {
                String classpathLine = it.trim()
                if (classpathLine.contains('*') || classpathLine.contains('?')) {
                    classpath.addAll(workspace.list(classpathLine).collect { createClasspathURL(it) })
                } else {
                    classpath << createClasspathURL(workspace.child(classpathLine))
                }
            }
        }
        if (usingScriptText) {
            URL[] urlRoots = ([createWorkspaceUrl()] + classpath) as URL[]
            ScriptRequest request = new ScriptRequest(scriptText, urlRoots, ignoreExisting)
            scriptRequests.add(request)
        } else {
            targets.split('\n').each { String target ->
                String expandedTarget = env.expand(target)
                String normalizedPath = FilenameUtils.normalize(expandedTarget, true)
                FilePath[] filePaths = workspace.list(normalizedPath)
                if (filePaths.length == 0 && !ignoreMissingFiles) {
                    throw new DslException("no Job DSL script(s) found at ${target}")
                }
                for (FilePath filePath : filePaths) {
                    URL[] urlRoots = ([createWorkspaceUrl(filePath.parent)] + classpath) as URL[]
                    ScriptRequest request = new ScriptRequest(
                            readFile(filePath), urlRoots, ignoreExisting, getAbsolutePath(filePath),
                            getAbsolutePath(workspace)
                    )
                    scriptRequests.add(request)
                }
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

    @SuppressWarnings('UnnecessaryGetter')
    @PackageScope
    static String getAbsolutePath(FilePath filePath) {
        // see JENKINS-33723
        FilenameUtils.normalize(filePath.getRemote(), true)
    }

    private URL createClasspathURL(FilePath filePath) {
        if (filePath.isRemote()) {
            if (filePath.directory) {
                createWorkspaceUrl(filePath)
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

    private URL createWorkspaceUrl() {
        new URL(null, 'workspace:/', new WorkspaceUrlHandler(workspace))
    }

    private URL createWorkspaceUrl(FilePath filePath) {
        String relativePath = getAbsolutePath(filePath) - getAbsolutePath(workspace)
        String slash = filePath.directory ? '/' : ''
        new URL(createWorkspaceUrl(), "$relativePath$slash")
    }

    private static File copyToLocal(FilePath filePath) {
        File file = File.createTempFile('jobdsl', '.jar')
        filePath.copyTo(new FilePath(file))
        file
    }

    private static String readFile(FilePath filePath) {
        InputStream inputStream = filePath.read()
        try {
            return inputStream.getText('UTF-8')
        } finally {
            inputStream.close()
        }
    }
}
