package javaposse.jobdsl.dsl

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ScriptRequest {
    // Starting Object
    final String location

    // Starting Script
    final String body

    // Where can we load objects from, file://. or http://server/ or workspace://JOBNAME/
    final URL[] urlRoots

    // Ignore existing jobs
    final boolean ignoreExisting

    // Path to the script in the local filesystem, optional
    final String scriptPath

    ScriptRequest(String body) {
        this(null, body, new File('.').toURI().toURL())
    }

    ScriptRequest(String location, String body, URL urlRoot, boolean ignoreExisting = false,
                  String scriptPath = null) {
        this(location, body, [urlRoot] as URL[], ignoreExisting, scriptPath)
    }

    ScriptRequest(String location, String body, URL[] urlRoots, boolean ignoreExisting = false,
                  String scriptPath = null) {
        this.location = location
        this.body = body
        this.urlRoots = urlRoots
        this.ignoreExisting = ignoreExisting
        this.scriptPath = scriptPath
    }
}
