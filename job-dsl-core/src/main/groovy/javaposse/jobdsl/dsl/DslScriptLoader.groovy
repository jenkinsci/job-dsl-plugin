package javaposse.jobdsl.dsl

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.InvokerHelper

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Runs provided DSL scripts via an external JObManager
 */
class DslScriptLoader {
    private static final Logger LOGGER = Logger.getLogger(DslScriptLoader.name)
    private static final Comparator<? super Item> ITEM_COMPARATOR = new ItemProcessingOrderComparator()

    static JobParent runDslEngineForParent(ScriptRequest scriptRequest,
                                           JobManagement jobManagement) throws IOException {
        ClassLoader parentClassLoader = DslScriptLoader.classLoader
        CompilerConfiguration config = createCompilerConfiguration(jobManagement)

        // Otherwise baseScript won't take effect
        GroovyClassLoader cl = new GroovyClassLoader(parentClassLoader, config)

        // Add static imports of a few common types, like JobType
        ImportCustomizer icz = new ImportCustomizer()
        icz.addStaticStars('javaposse.jobdsl.dsl.JobType')
        icz.addStaticStars('javaposse.jobdsl.dsl.ViewType')
        icz.addStaticStars('javaposse.jobdsl.dsl.helpers.common.MavenContext.LocalRepositoryLocation')
        config.addCompilationCustomizers(icz)

        GroovyScriptEngine engine = new GroovyScriptEngine(scriptRequest.urlRoots, cl)
        engine.config = config

        Binding binding = createBinding(jobManagement)

        JobParent jp
        try {
            Script script
            if (scriptRequest.body != null) {
                Class cls = engine.groovyClassLoader.parseClass(scriptRequest.body)
                script = InvokerHelper.createScript(cls, binding)
            } else {
                script = engine.createScript(scriptRequest.location, binding)
            }
            assert script instanceof JobParent

            jp = (JobParent) script
            jp.setJm(jobManagement)

            binding.setVariable('jobFactory', jp)

            script.run()
        } catch (e) { // ResourceException or ScriptException
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e)
            } else {
                throw new IOException('Unable to run script', e)
            }
        }
        jp
    }

    /**
     * For testing a string directly.
     */
    static GeneratedItems runDslEngine(String scriptBody, JobManagement jobManagement) throws IOException {
        ScriptRequest scriptRequest = new ScriptRequest(null, scriptBody, new File('.').toURI().toURL())
        runDslEngine(scriptRequest, jobManagement)
    }

    static GeneratedItems runDslEngine(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        JobParent jp = runDslEngineForParent(scriptRequest, jobManagement)
        LOGGER.log(Level.FINE, String.format('Ran script and got back %s', jp))

        GeneratedItems generatedItems = new GeneratedItems()
        generatedItems.jobs = extractGeneratedJobs(jp, scriptRequest.ignoreExisting)
        generatedItems.views = extractGeneratedViews(jp, scriptRequest.ignoreExisting)

        scheduleJobsToRun(jp.queueToBuild, jobManagement)

        generatedItems
    }

    private static Set<GeneratedJob> extractGeneratedJobs(JobParent jp, boolean ignoreExisting) {
        // Iterate jobs which were setup, save them, and convert to a serializable form
        Set<GeneratedJob> generatedJobs = []
        if (jp != null) {
            jp.referencedJobs.sort(ITEM_COMPARATOR).each { Item job ->
                String xml = job.xml
                LOGGER.log(Level.FINE, String.format('Saving job %s as %s', job.name, xml))
                jp.jm.createOrUpdateConfig(job.name, xml, ignoreExisting)
                String templateName = job instanceof Job ? ((Job) job).templateName : null
                generatedJobs.add(new GeneratedJob(templateName, job.name))
            }
        }
        generatedJobs
    }

    private static Set<GeneratedView> extractGeneratedViews(JobParent jp, boolean ignoreExisting) {
        Set<GeneratedView> generatedViews = []
        jp.referencedViews.each { View view ->
            String xml = view.xml
            LOGGER.log(Level.FINE, String.format('Saving view %s as %s', view.name, xml))
            jp.jm.createOrUpdateView(view.name, xml, ignoreExisting)
            GeneratedView gv = new GeneratedView(view.name)
            generatedViews.add(gv)
        }
        generatedViews
    }

    static void scheduleJobsToRun(List<String> jobNames, JobManagement jobManagement) {
        Map<String, Throwable> exceptions = [:]
        jobNames.each { String jobName ->
            try {
                jobManagement.queueJob(jobName)
            } catch (e) {
                exceptions.put(jobName, e)
            }
        }
        if (!exceptions.isEmpty()) {
            LOGGER.warning('Trouble schedule some jobs')
            exceptions.each { String jobName, Throwable exception ->
                LOGGER.throwing('DslScriptLoader', jobName, exception)
            }
        }
    }

    private static Binding createBinding(JobManagement jobManagement) {
        Binding binding = new Binding()
        binding.setVariable('out', jobManagement.outputStream) // Works for println, but not System.out

        Map<String, String> params = jobManagement.parameters
        params.each { String key, String value ->
            LOGGER.fine(String.format('Binding %s to %s', key, value))
            binding.setVariable(key, value)
        }
        binding
    }

    private static CompilerConfiguration createCompilerConfiguration(JobManagement jobManagement) {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT)
        config.scriptBaseClass = 'javaposse.jobdsl.dsl.JobParent'

        // Import some of our helper classes so that user doesn't have to.
        ImportCustomizer icz = new ImportCustomizer()
        icz.addImports('javaposse.jobdsl.dsl.helpers.Permissions')
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.ArchiveXUnitContext.ThresholdMode')
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior')
        icz.addImports('javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir')
        icz.addImports('javaposse.jobdsl.dsl.views.ListView.StatusFilter')
        icz.addImports('javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle')
        config.addCompilationCustomizers(icz)

        config.output = new PrintWriter(jobManagement.outputStream) // This seems to do nothing
        config
    }
}
