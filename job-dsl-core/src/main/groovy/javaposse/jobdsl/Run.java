package javaposse.jobdsl;

import javaposse.jobdsl.dsl.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Able to run from the command line to test out. Leverage FileJobManagement
 */
public class Run {

    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            throw new RuntimeException("Script name is required");
        }

        String scriptName = args[0];

        File cwd = new File(".");
        ScriptRequest request = new ScriptRequest(scriptName, null, cwd.toURL(), false);
        FileJobManagement jm = new FileJobManagement(cwd);
        jm.getParameters().putAll(System.getenv());
        for(Map.Entry entry: System.getProperties().entrySet()) {
            jm.getParameters().put(entry.getKey().toString(), entry.getValue().toString());
        }
        Set<GeneratedJob> generatedJobs = DslScriptLoader.runDslEngine(request, jm);

        for(GeneratedJob job: generatedJobs) {
            System.out.println("Generated: " + job);
        }
    }
}
