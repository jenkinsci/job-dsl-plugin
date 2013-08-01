package javaposse.jobdsl;

import javaposse.jobdsl.dsl.*;

import javax.annotation.Generated;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Able to run from the command line to test out. Leverage FileJobManagement
 */
public class Run {

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            throw new RuntimeException("Script name is required");
        }

        File cwd = new File(".");
        URL cwdURL = cwd.toURI().toURL();

        FileJobManagement jm = new FileJobManagement(cwd);
        jm.getParameters().putAll(System.getenv());
        for(Map.Entry entry: System.getProperties().entrySet()) {
            jm.getParameters().put(entry.getKey().toString(), entry.getValue().toString());
        }

        Collection<String> scripts = new ArrayList<String>();
        for(String arg: args) {
            if(arg.startsWith("--")) {
                scripts.add(arg);
            }
        }

        for(String scriptName: args) {
            ScriptRequest request = new ScriptRequest(scriptName, null, cwdURL, false);
            Set<GeneratedJob> generatedJobs = DslScriptLoader.runDslEngine(request, jm);

            for(GeneratedJob job: generatedJobs) {
                System.out.println("From "+ scriptName + ", Generated: " + job);
            }
        }
    }
}
