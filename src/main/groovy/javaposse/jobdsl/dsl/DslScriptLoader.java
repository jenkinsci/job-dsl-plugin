package javaposse.jobdsl.dsl;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.google.common.collect.Sets;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs provided DSL scripts via an external JObManager
 */
public class DslScriptLoader {
    private static final Logger LOGGER = Logger.getLogger(DslScriptLoader.class.getName());

    /**
     * Runs the provided DSL script through the provided job manager.
     * 
     * @param scriptContent the contents of the DSL script
     * @param jobManagement the instance of JobManagement which processes the resulting Jenkins job config changes
     */
    public static Set<GeneratedJob> runDsl(String scriptContent, JobManagement jobManagement) {
        Binding binding = new Binding();
        binding.setVariable("secretJobManagement", jobManagement); // TODO Find better way of getting this variable into JobParent

        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.setScriptBaseClass("javaposse.jobdsl.dsl.JobParent");

        JobParent jp = parseScript(scriptContent, config, binding);

        // Iterate jobs which were setup, save them, and convert to a serializable form
        Set<GeneratedJob> generatedJobs = Sets.newHashSet();
        if (jp != null) {
            for(Job job: jp.getReferencedJobs()) {
                try {
                    jobManagement.createOrUpdateConfig(job.getName(), job.getXml());
                } catch (JobNameNotProvidedException jnnpe) {
                    // TODO: What is the sensible thing to do here?
                } catch (JobConfigurationMissingException jcmex) {
                    // TODO: What is the sensible thing to do here?
                }
                GeneratedJob gj = new GeneratedJob(job.getTemplateName(), job.getName());
                generatedJobs.add(gj);
            }
        }
        return generatedJobs;
    }

    static JobParent parseScript(String scriptContent, CompilerConfiguration config, Binding binding) throws CompilationFailedException {
        ClassLoader parent = DslScriptLoader.class.getClassLoader(); // TODO Setup different classloader
        GroovyShell shell = new GroovyShell(parent, binding, config);
        Script script = shell.parse(scriptContent);
        if (!(script instanceof JobParent)) {
            // Assume an empty script
            return null;
        }
        Object result = script.run(); // Probably the last job
        LOGGER.log(Level.FINE, String.format("Ran script and got back %s", result));
        return (JobParent) script;
    }

}
