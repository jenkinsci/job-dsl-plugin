package javaposse.jobdsl.dsl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
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

    public static JobParent runDslEngineForParent(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        ClassLoader parentClassLoader = DslScriptLoader.class.getClassLoader();
        CompilerConfiguration config = createCompilerConfiguration(jobManagement);

        // Otherwise baseScript won't take effect
        GroovyClassLoader cl = new GroovyClassLoader(parentClassLoader, config);

        // Add static imports of a few common types, like JobType
        ImportCustomizer icz = new ImportCustomizer();
        icz.addStaticStars("javaposse.jobdsl.dsl.JobType");
        icz.addStaticStars("javaposse.jobdsl.dsl.ViewType");
        icz.addStaticStars("javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation");
        config.addCompilationCustomizers(icz);

        GroovyScriptEngine engine = new GroovyScriptEngine(new URL[]{scriptRequest.urlRoot}, cl);

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
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            } else {
                throw new IOException("Unable to run script", e);
            }
        }
        return jp;
    }

    /**
     * For testing a string directly.
     */
    static GeneratedItems runDslEngine(String scriptBody, JobManagement jobManagement) throws IOException {
        ScriptRequest scriptRequest = new ScriptRequest(null, scriptBody, new File(".").toURI().toURL());
        return runDslEngine(scriptRequest, jobManagement);
    }

    public static GeneratedItems runDslEngine(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        JobParent jp = runDslEngineForParent(scriptRequest, jobManagement);
        LOGGER.log(Level.FINE, String.format("Ran script and got back %s", jp));

        GeneratedItems generatedItems = new GeneratedItems();
        generatedItems.setJobs(extractGeneratedJobs(jp, scriptRequest.ignoreExisting));
        generatedItems.setViews(extractGeneratedViews(jp, scriptRequest.ignoreExisting));

        scheduleJobsToRun(jp.getQueueToBuild(), jobManagement);

        return generatedItems;
    }

    private static Set<GeneratedJob> extractGeneratedJobs(JobParent jp, boolean ignoreExisting) {
        // Iterate jobs which were setup, save them, and convert to a serializable form
        Set<GeneratedJob> generatedJobs = Sets.newLinkedHashSet();
        if (jp != null) {
            List<Item> referencedItems = Lists.newArrayList(jp.getReferencedJobs()); // As List
            Collections.sort(referencedItems, ITEM_COMPARATOR);
            for (Item job : referencedItems) {
                String xml = job.getXml();
                LOGGER.log(Level.FINE, String.format("Saving job %s as %s", job.getName(), xml));
                boolean created = jp.getJm().createOrUpdateConfig(job.getName(), xml, ignoreExisting);
                String templateName = job instanceof Job ? ((Job) job).getTemplateName() : null;
                generatedJobs.add(new GeneratedJob(templateName, job.getName(), created));
            }
        }
        return generatedJobs;
    }

    private static Set<GeneratedView> extractGeneratedViews(JobParent jp, boolean ignoreExisting) {
        Set<GeneratedView> generatedViews = Sets.newLinkedHashSet();
        for (View view : jp.getReferencedViews()) {
            String xml = view.getXml();
            LOGGER.log(Level.FINE, String.format("Saving view %s as %s", view.getName(), xml));
            jp.getJm().createOrUpdateView(view.getName(), xml, ignoreExisting);
            GeneratedView gv = new GeneratedView(view.getName());
            generatedViews.add(gv);
        }
        return generatedViews;
    }

    static void scheduleJobsToRun(List<String> jobNames, JobManagement jobManagement) {
        Map<String, Throwable> exceptions = Maps.newHashMap();
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
        icz.addImports("javaposse.jobdsl.dsl.helpers.Permissions");
        icz.addImports("javaposse.jobdsl.dsl.helpers.publisher.ArchiveXUnitContext.ThresholdMode");
        icz.addImports("javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior");
        icz.addImports("javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir");
        icz.addImports("javaposse.jobdsl.dsl.views.ListView.StatusFilter");
        icz.addImports("javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle");
        config.addCompilationCustomizers(icz);

        config.setOutput(new PrintWriter(jobManagement.getOutputStream())); // This seems to do nothing
        return config;
    }
}
