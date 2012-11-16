package javaposse.jobdsl.dsl;

import java.net.URL;

public class ScriptRequest {
    public ScriptRequest(String location, String body, URL urlRoot) {
        this.location = location;
        this.body = body;
        this.urlRoot = urlRoot;
    }

    // Starting Object
    String location;

    // Starting Script
    String body;

    // Where can we load objects from
    //ResourceConnector resourceConnector; // OR
    URL urlRoot; // file://. or http://server/ or workspace://JOBNAME/
}
