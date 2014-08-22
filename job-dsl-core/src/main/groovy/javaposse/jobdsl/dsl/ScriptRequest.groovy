package javaposse.jobdsl.dsl;

import java.net.URL;

public class ScriptRequest {
    public ScriptRequest(String location, String body, URL urlRoot) {
        this(location, body, new URL[]{urlRoot});
    }

    public ScriptRequest(String location, String body, URL[] urlRoots) {
        this(location, body, urlRoots, false);
    }

    public ScriptRequest(String location, String body, URL urlRoot, boolean ignoreExisting) {
        this(location, body, new URL[]{urlRoot}, ignoreExisting);
    }

    public ScriptRequest(String location, String body, URL[] urlRoots, boolean ignoreExisting) {
        this.location = location;
        this.body = body;
        this.urlRoots = urlRoots;
        this.ignoreExisting = ignoreExisting;
    }

    // Starting Object
    public String location;

    // Starting Script
    public String body;

    // Where can we load objects from
    //ResourceConnector resourceConnector; // OR
    public URL[] urlRoots; // file://. or http://server/ or workspace://JOBNAME/

    // Ignore existing jobs
    public boolean ignoreExisting;
}
