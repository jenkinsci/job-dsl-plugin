package javaposse.jobdsl.dsl

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ScriptRequest {
    // Starting Script
    final String body

    // Where can we load objects from, file://. or http://server/ or workspace://JOBNAME/
    final URL[] urlRoots

    // Ignore existing jobs
    final boolean ignoreExisting

    // Path to the script in the local filesystem, optional
    final String scriptPath

    // Base path, e.g. the workspace or current working directory
    final String basePath

    ScriptRequest(String body) {
        this(body, new File('.').toURI().toURL())
    }

    ScriptRequest(String body, URL urlRoot, boolean ignoreExisting = false, String scriptPath = null,
                  String basePath = null) {
        this(body, [urlRoot] as URL[], ignoreExisting, scriptPath, basePath)
    }

    ScriptRequest(String body, URL[] urlRoots, boolean ignoreExisting = false, String scriptPath = null,
                  String basePath = null) {
        this.body = body
        this.urlRoots = urlRoots
        this.ignoreExisting = ignoreExisting
        this.scriptPath = scriptPath
        this.basePath = basePath
    }

    /**
     * Gets the script's file name portion without directories.
     *
     * For example, "foo.txt" for "/abc/foo.txt"
     *
     * @since 1.58
     * @return the script`s file name or {@code null} if no file name has been provided
     */
    String getScriptName() {
        if (!scriptPath) {
            return null
        }
        int index = Math.max(scriptPath.lastIndexOf('/'), scriptPath.lastIndexOf('\\')) + 1
        scriptPath[index..-1]
    }

    /**
     * @since 1.71
     */
    String getRelativeScriptPath() {
        if (scriptPath && basePath && scriptPath.startsWith(basePath)) {
            return scriptPath[(basePath.length() + 1)..-1]
        }
        scriptPath
    }

    /**
     * Gets the script's file name portion without extension.
     *
     * For example, "foo" for "foo.txt" and "foo.tar" for "foo.tar.gz".
     *
     * @since 1.58
     * @return the script`s file name without extension or {@code null} if no file name has been provided
     */
    String getScriptBaseName() {
        String fileName = scriptName
        if (!fileName) {
            return null
        }
        int idx = fileName.lastIndexOf('.')
        idx > -1 ? fileName[0..idx - 1] : fileName
    }
}
