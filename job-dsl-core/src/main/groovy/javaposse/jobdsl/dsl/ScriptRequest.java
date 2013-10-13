package javaposse.jobdsl.dsl;

import java.net.URL;

public class ScriptRequest {
    public ScriptRequest(String location, String body, URL urlRoot) {
        this(location, body, urlRoot, false);
    }

    public ScriptRequest(String location, String body, URL urlRoot, boolean ignoreExisting) {
        this.location = location;
        this.body = body;
        this.urlRoot = urlRoot;
        this.ignoreExisting = ignoreExisting;
    }

    // Starting Object
    public String location;

    // Starting Script
    public String body;

    // Where can we load objects from
    //ResourceConnector resourceConnector; // OR
    public URL urlRoot; // file://. or http://server/ or workspace://JOBNAME/

    // Ignore existing jobs
    public boolean ignoreExisting;
}
