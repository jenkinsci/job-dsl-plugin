package javaposse.jobdsl.dsl;

import java.net.URL;

public class ScriptRequest {
    public ScriptRequest(String location, URL urlRoot) {
        this.location = location;
        this.urlRoot = urlRoot;
    }

    // Starting Object
    String location;

    // Where can we load objects from
    //ResourceConnector resourceConnector; // OR
    URL urlRoot; // file://. or http://server/ or workspace://JOBNAME/
}
