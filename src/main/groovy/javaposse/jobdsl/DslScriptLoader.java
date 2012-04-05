package javaposse.jobdsl;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DslScriptLoader {
    private static final Logger LOGGER = Logger.getLogger(DslScriptLoader.class.getName());

    public static void runDsl(String scriptContent, JobManagement jobManagement) {
        Binding binding = new Binding();
        binding.setVariable("secretJobManagement", jobManagement); // TODO Find better way of getting this variable into JobParent

        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.setScriptBaseClass("javaposse.jobdsl.JobParent");

        parseScript(scriptContent, config, binding);
    }

    public static Object parseScript(String scriptContent, CompilerConfiguration config, Binding binding) throws CompilationFailedException {
        ClassLoader parent = DslScriptLoader.class.getClassLoader(); // TODO Setup different classloader
        GroovyShell shell = new GroovyShell(parent, binding, config);
        Script script = shell.parse(scriptContent);
        Object result = script.run();
        LOGGER.log(Level.FINE, String.format("Ran script and got back %s", result));
        return result;
    }

}
