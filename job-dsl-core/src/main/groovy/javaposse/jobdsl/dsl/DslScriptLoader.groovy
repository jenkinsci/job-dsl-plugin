package javaposse.jobdsl.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs provided DSL scripts via an external JObManager
 */
public class DslScriptLoader {
    private static final Logger LOGGER = Logger.getLogger(DslScriptLoader.class.getName());
    private static final Comparator<? super Item> ITEM_COMPARATOR = new ItemProcessingOrderComparator();

    private static GeneratedItems runDslEngineForParent(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        ClassLoader parentClassLoader = DslScriptLoader.class.getClassLoader();
        CompilerConfiguration config = createCompilerConfiguration(jobManagement);

        // Otherwise baseScript won't take effect
        GroovyClassLoader cl = new GroovyClassLoader(parentClassLoader, config);
        try {
            GroovyScriptEngine engine = new GroovyScriptEngine(scriptRequest.getUrlRoots(), cl);
            try {
                engine.setConfig(config);

                Binding binding = createBinding(jobManagement);

                JobParent jp;
                try {
                    Script script;
                    if (scriptRequest.getBody() != null) {
                        jobManagement.getOutputStream().println("Processing provided DSL script");
                        Class cls = engine.getGroovyClassLoader().parseClass(scriptRequest.getBody(), "script");
                        script = InvokerHelper.createScript(cls, binding);
                    } else {
                        jobManagement.getOutputStream().printf("Processing DSL script %s\n", scriptRequest.getLocation());
                        if (!isValidScriptName(scriptRequest.getLocation())) {
                            jobManagement.logDeprecationWarning(
                                    "script names may only contain letters, digits and underscores, but may not start with a digit; support for arbitrary names",
                                    scriptRequest.getLocation(),
                                    -1
                            );
                        }
                        script = engine.createScript(scriptRequest.getLocation(), binding);
                    }
                    assert script instanceof JobParent;

                    jp = (JobParent) script;
                    jp.setJm(jobManagement);

                    binding.setVariable("jobFactory", jp);

                    script.run();
                } catch (CompilationFailedException e) {
                    throw new DslException(e.getMessage(), e);
                } catch (GroovyRuntimeException e) {
                    throw new DslScriptException(e.getMessage(), e);
                } catch (ResourceException e) {
                    throw new IOException("Unable to run script", e);
                } catch (ScriptException e) {
                    throw new IOException("Unable to run script", e);
                }

                GeneratedItems generatedItems = new GeneratedItems();
                generatedItems.setConfigFiles(extractGeneratedConfigFiles(jp, scriptRequest.getIgnoreExisting()));
                generatedItems.setJobs(extractGeneratedJobs(jp, scriptRequest.getIgnoreExisting()));
                generatedItems.setViews(extractGeneratedViews(jp, scriptRequest.getIgnoreExisting()));
                generatedItems.setUserContents(extractGeneratedUserContents(jp, scriptRequest.getIgnoreExisting()));

                scheduleJobsToRun(jp.getQueueToBuild(), jobManagement);

                return generatedItems;
            } finally {
                if (engine.getGroovyClassLoader() instanceof Closeable) {
                    ((Closeable) engine.getGroovyClassLoader()).close();
                }
            }
        } finally {
            if (cl instanceof Closeable) {
                ((Closeable) cl).close();
            }
        }
    }

    private static boolean isValidScriptName(String scriptFile) {
        int idx = scriptFile.lastIndexOf('.');
        if (idx > -1) {
            scriptFile = scriptFile.substring(0, idx);
        }
        if (scriptFile.length() == 0 || !Character.isJavaIdentifierStart(scriptFile.charAt(0))) {
            return false;
        }
        for (int i = 1; i < scriptFile.length(); i += 1) {
            if (!Character.isJavaIdentifierPart(scriptFile.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * For testing a string directly.
     */
    static GeneratedItems runDslEngine(String scriptBody, JobManagement jobManagement) throws IOException {
        ScriptRequest scriptRequest = new ScriptRequest(null, scriptBody, new File(".").toURI().toURL());
        return runDslEngine(scriptRequest, jobManagement);
    }

    public static GeneratedItems runDslEngine(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        return runDslEngineForParent(scriptRequest, jobManagement);
    }

    private static Set<GeneratedJob> extractGeneratedJobs(JobParent jp, boolean ignoreExisting) throws IOException {
        // Iterate jobs which were setup, save them, and convert to a serializable form
        Set<GeneratedJob> generatedJobs = new LinkedHashSet<GeneratedJob>();
        if (jp != null) {
            List<Item> referencedItems = new ArrayList<Item>(jp.getReferencedJobs()); // As List
            Collections.sort(referencedItems, ITEM_COMPARATOR);
            for (Item item : referencedItems) {
                String xml = item.getXml();
                LOGGER.log(Level.FINE, String.format("Saving item %s as %s", item.getName(), xml));
                if (item instanceof Job) {
                    Job job = (Job) item;
                    if (job.getPreviousNamesRegex() != null) {
                        jp.getJm().renameJobMatching(job.getPreviousNamesRegex(), job.getName());
                    }
                }
                jp.getJm().createOrUpdateConfig(item, ignoreExisting);
                String templateName = item instanceof Job ? ((Job) item).getTemplateName() : null;
                generatedJobs.add(new GeneratedJob(templateName, item.getName()));
            }
        }
        return generatedJobs;
    }

    private static Set<GeneratedView> extractGeneratedViews(JobParent jp, boolean ignoreExisting) {
        Set<GeneratedView> generatedViews = new LinkedHashSet<GeneratedView>();
        for (View view : jp.getReferencedViews()) {
            String xml = view.getXml();
            LOGGER.log(Level.FINE, String.format("Saving view %s as %s", view.getName(), xml));
            jp.getJm().createOrUpdateView(view.getName(), xml, ignoreExisting);
            GeneratedView gv = new GeneratedView(view.getName());
            generatedViews.add(gv);
        }
        return generatedViews;
    }

    private static Set<GeneratedConfigFile> extractGeneratedConfigFiles(JobParent jp, boolean ignoreExisting) {
        Set<GeneratedConfigFile> generatedConfigFiles = new LinkedHashSet<GeneratedConfigFile>();
        for (ConfigFile configFile : jp.getReferencedConfigFiles()) {
            LOGGER.log(Level.FINE, String.format("Saving config file %s", configFile.getName()));
            String id = jp.getJm().createOrUpdateConfigFile(configFile, ignoreExisting);
            generatedConfigFiles.add(new GeneratedConfigFile(id, configFile.getName()));
        }
        return generatedConfigFiles;
    }

    private static Set<GeneratedUserContent> extractGeneratedUserContents(JobParent jp, boolean ignoreExisting) {
        Set<GeneratedUserContent> generatedUserContents = new LinkedHashSet<GeneratedUserContent>();
        for (UserContent userContent : jp.getReferencedUserContents()) {
            LOGGER.log(Level.FINE, String.format("Saving user content %s", userContent.getPath()));
            jp.getJm().createOrUpdateUserContent(userContent, ignoreExisting);
            generatedUserContents.add(new GeneratedUserContent(userContent.getPath()));
        }
        return generatedUserContents;
    }

    static void scheduleJobsToRun(List<String> jobNames, JobManagement jobManagement) {
        Map<String, Throwable> exceptions = new HashMap<String, Throwable>();
        for (String jobName : jobNames) {
            try {
                jobManagement.queueJob(jobName);
            } catch (Exception e) {
                exceptions.put(jobName, e);
            }
        }
        if (!exceptions.isEmpty()) {
            LOGGER.warning("Trouble schedule some jobs");
            for (Map.Entry<String, Throwable> entry : exceptions.entrySet()) {
                LOGGER.throwing("DslScriptLoader", entry.getKey(), entry.getValue());
            }
        }
    }

    private static Binding createBinding(JobManagement jobManagement) {
        Binding binding = new Binding();
        binding.setVariable("out", jobManagement.getOutputStream()); // Works for println, but not System.out

        Map<String, String> params = jobManagement.getParameters();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            LOGGER.fine(String.format("Binding %s to %s", entry.getKey(), entry.getValue()));
            binding.setVariable(entry.getKey(), entry.getValue());
        }
        return binding;
    }

    private static CompilerConfiguration createCompilerConfiguration(JobManagement jobManagement) {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.setScriptBaseClass("javaposse.jobdsl.dsl.JobParent");

        // Import some of our helper classes so that user doesn't have to.
        ImportCustomizer icz = new ImportCustomizer();
        icz.addImports("javaposse.jobdsl.dsl.helpers.publisher.ArchiveXUnitContext.ThresholdMode");
        icz.addImports("javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior");
        icz.addImports("javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir");
        icz.addImports("javaposse.jobdsl.dsl.views.ListView.StatusFilter");
        icz.addImports("javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle");
        icz.addImports("javaposse.jobdsl.dsl.views.DeliveryPipelineView.Sorting");
        icz.addImports("javaposse.jobdsl.dsl.views.jobfilter.Status");
        icz.addImports("javaposse.jobdsl.dsl.views.jobfilter.MatchType");
        icz.addImports("javaposse.jobdsl.dsl.views.jobfilter.RegexMatchValue");
        icz.addImports("javaposse.jobdsl.dsl.helpers.scm.SvnCheckoutStrategy");
        icz.addImports("javaposse.jobdsl.dsl.helpers.scm.SvnDepth");
        icz.addImports("javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation");
        icz.addImports("javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes");
        config.addCompilationCustomizers(icz);

        config.setOutput(new PrintWriter(jobManagement.getOutputStream())); // This seems to do nothing
        return config;
    }
}
