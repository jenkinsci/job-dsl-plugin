package javaposse.jobdsl;

import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.FileJobManagement;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.GeneratedView;
import javaposse.jobdsl.dsl.ScriptRequest;
import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
            GeneratedItems generatedItems = DslScriptLoader.runDslEngine(request, jm);

            for(GeneratedJob job: generatedItems.getJobs()) {
                System.out.println("From "+ scriptName + ", Generated item: " + job);
            }
            for(GeneratedView view: generatedItems.getViews()) {
                System.out.println("From "+ scriptName + ", Generated view: " + view);
            }
        }
    }
}
