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

    // Delete and re-create existing views
    final boolean deleteExistingViews

    // Path to the script in the local filesystem, optional
    final String scriptPath

    ScriptRequest(String body) {
        this(body, new File('.').toURI().toURL())
    }

    ScriptRequest(String body, URL urlRoot, boolean ignoreExisting = false, String scriptPath = null) {
        this(body, [urlRoot] as URL[], ignoreExisting, false,  scriptPath)
    }

    ScriptRequest(String body, URL[] urlRoots, boolean ignoreExisting = false,
                  boolean deleteExistingViews = false, String scriptPath = null) {
        this.body = body
        this.urlRoots = urlRoots
        this.ignoreExisting = ignoreExisting
        this.deleteExistingViews = deleteExistingViews
        this.scriptPath = scriptPath
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
