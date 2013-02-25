package javaposse.jobdsl.dsl;

import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyScriptEngine;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.google.common.collect.Sets;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs provided DSL scripts via an external JObManager
 */
public class DslScriptLoader {
    private static final Logger LOGGER = Logger.getLogger(DslScriptLoader.class.getName());

    public static Set<GeneratedJob> runDslEngine(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        // TODO Setup different classloader, especially for Grape to work, which needs a RootLoader or a GroovyLoader
        ClassLoader parentClassLoader = DslScriptLoader.class.getClassLoader();
        CompilerConfiguration config = createCompilerConfiguration(jobManagement);

        // Otherwise baseScript won't take effect
        GroovyClassLoader cl = new GroovyClassLoader(parentClassLoader, config);

        GroovyScriptEngine engine = //scriptRequest.resourceConnector!=null?
                //new GroovyScriptEngine(scriptRequest.resourceConnector, parentClassLoader):
                new GroovyScriptEngine(new URL[] { scriptRequest.urlRoot } , cl);

        engine.setConfig(config);

        Binding binding = createBinding(jobManagement);

        JobParent jp;
        try {
            Script script;
            if (scriptRequest.body != null) {
                Class cls = engine.getGroovyClassLoader().parseClass(scriptRequest.body);
                script = InvokerHelper.createScript(cls, binding);
            } else {
                script = engine.createScript(scriptRequest.location, binding);
            }
            assert script instanceof JobParent;

            jp = (JobParent) script;
            jp.setJm(jobManagement);

            binding.setVariable("jobFactory", jp);

            script.run();
        } catch (Exception e) { // ResourceException or ScriptException
            if(e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            } else {
                throw new IOException("Unable to run script", e);
            }
        }

        LOGGER.log(Level.FINE, String.format("Ran script and got back %s", jp));

        Set<GeneratedJob> generatedJobs = extractGeneratedJobs(jp, scriptRequest.ignoreExisting);
        return generatedJobs;

    }
    /**
     * Runs the provided DSL script through the provided job manager.
     *
     * @param scriptContent the contents of the DSL script
     * @param jobManagement the instance of JobManagement which processes the resulting Jenkins job config changes
     */
    @Deprecated
    public static Set<GeneratedJob> runDslShell(String scriptContent, JobManagement jobManagement) {
        Binding binding = createBinding(jobManagement);

        CompilerConfiguration config = createCompilerConfiguration(jobManagement);

        // TODO Setup different classloader, especially for Grape to work, which needs a RootLoader or a GroovyLoader
        ClassLoader parent = DslScriptLoader.class.getClassLoader();

        GroovyShell shell = new GroovyShell(parent, binding, config);
        Script script = shell.parse(scriptContent);
        if (!(script instanceof JobParent)) {
            // Assume an empty script
            return null;
        }
        ((JobParent) script).setJm(jobManagement);
        Object result = script.run(); // Probably the last job
        LOGGER.log(Level.FINE, String.format("Ran script and got back %s", result));
        JobParent jp =  (JobParent) script;

        Set<GeneratedJob> generatedJobs = extractGeneratedJobs(jp, false);

        return generatedJobs;
    }

    private static Set<GeneratedJob> extractGeneratedJobs(JobParent jp, boolean ignoreExisting) {
        // Iterate jobs which were setup, save them, and convert to a serializable form
        Set<GeneratedJob> generatedJobs = Sets.newLinkedHashSet();
        if (jp != null) {
            for(Job job: jp.getReferencedJobs()) {
                try {
                    String xml = job.getXml();
                    LOGGER.log(Level.FINE, String.format("Saving job %s as %s", job.getName(), xml));
                    boolean created = jp.getJm().createOrUpdateConfig(job.getName(), xml, ignoreExisting);
                    GeneratedJob gj = new GeneratedJob(job.getTemplateName(), job.getName(), created);
                    generatedJobs.add(gj);
                } catch( Exception e) {  // org.xml.sax.SAXException, java.io.IOException
                    if (e instanceof RuntimeException) {
                        throw ((RuntimeException) e);
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return generatedJobs;
    }

    private static Binding createBinding(JobManagement jobManagement) {
        Binding binding = new Binding();
        //binding.setVariable("secretJobManagement", jobManagement); // TODO Find better way of getting this variable into JobParent
        binding.setVariable("out", jobManagement.getOutputStream() ); // Works for println, but not System.out

        binding.setVariable("testing", "THe string which says testing" );

        Map<String, String> params = jobManagement.getParameters();
        for(Map.Entry<String,String> entry: params.entrySet()) {
            LOGGER.fine(String.format("Binding %s to %s", entry.getKey(), entry.getValue()));
            binding.setVariable(entry.getKey(), entry.getValue());
        }
        return binding;
    }

    private static CompilerConfiguration createCompilerConfiguration(JobManagement jobManagement) {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.setScriptBaseClass("javaposse.jobdsl.dsl.JobParent");

        config.setOutput( new PrintWriter(jobManagement.getOutputStream())); // This seems to do nothing
        return config;
    }

}
