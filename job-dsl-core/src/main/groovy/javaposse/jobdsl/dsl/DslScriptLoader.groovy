package javaposse.jobdsl.dsl

import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.InvokerHelper

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Runs provided DSL scripts via an external {@link JobManagement}.
 */
class DslScriptLoader {

    private static final Logger LOGGER = Logger.getLogger(DslScriptLoader.name)
    private static final Comparator<? super Item> ITEM_COMPARATOR = new ItemProcessingOrderComparator()

    private final JobManagement jobManagement
    private final PrintStream logger

    /**
     * For testing a string directly.
     */
    static GeneratedItems runDslEngine(String scriptBody, JobManagement jobManagement) throws IOException {
        ScriptRequest scriptRequest = new ScriptRequest(null, scriptBody, new File('.').toURI().toURL())
        runDslEngine(scriptRequest, jobManagement)
    }

    static GeneratedItems runDslEngine(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        DslScriptLoader loader = new DslScriptLoader(jobManagement)
        loader.runScripts([scriptRequest])
    }

    DslScriptLoader(JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.logger = jobManagement.outputStream
    }

    GeneratedItems runScripts(Collection<ScriptRequest> scriptRequests) throws IOException {
        ClassLoader parentClassLoader = DslScriptLoader.classLoader
        CompilerConfiguration config = createCompilerConfiguration(jobManagement)

        // Otherwise baseScript won't take effect
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(parentClassLoader, config)

        try {
            runScriptsWithClassLoader(scriptRequests, groovyClassLoader, config)
        } finally {
            if (groovyClassLoader instanceof Closeable) {
                ((Closeable) groovyClassLoader).close()
            }
        }
    }

    private GeneratedItems runScriptsWithClassLoader(Collection<ScriptRequest> scriptRequests,
                                                     GroovyClassLoader groovyClassLoader,
                                                     CompilerConfiguration config) {
        GeneratedItems generatedItems = new GeneratedItems()

        // group requests that share the same classpath
        scriptRequests.groupBy { it.urlRoots*.toString().sort() }.values().each { List<ScriptRequest> requestSet ->
            GroovyScriptEngine engine
            try {
                engine = new GroovyScriptEngine(requestSet.first().urlRoots, groovyClassLoader)
                engine.config = config

                requestSet.each { ScriptRequest scriptRequest ->
                    JobParent jobParent = runScript(scriptRequest, engine)

                    boolean ignoreExisting = scriptRequest.ignoreExisting
                    generatedItems.configFiles.addAll(extractGeneratedConfigFiles(jobParent, ignoreExisting))
                    generatedItems.jobs.addAll(extractGeneratedJobs(jobParent, ignoreExisting))
                    generatedItems.views.addAll(extractGeneratedViews(jobParent, ignoreExisting))
                    generatedItems.userContents.addAll(extractGeneratedUserContents(jobParent, ignoreExisting))

                    scheduleJobsToRun(jobParent.queueToBuild)
                }

            } finally {
                if (engine?.groovyClassLoader instanceof Closeable) {
                    ((Closeable) engine.groovyClassLoader).close()
                }
            }
        }
        generatedItems
    }

    private JobParent runScript(ScriptRequest scriptRequest, GroovyScriptEngine engine) {
        LOGGER.log(Level.FINE, String.format("Request for ${scriptRequest.location}"))

        Binding binding = createBinding()
        try {
            Script script
            if (scriptRequest.body != null) {
                logger.println('Processing provided DSL script')
                Class cls = engine.groovyClassLoader.parseClass(scriptRequest.body, 'script')
                script = InvokerHelper.createScript(cls, binding)
            } else {
                logger.println("Processing DSL script ${scriptRequest.location}")
                checkValidScriptName(scriptRequest.location)
                checkCollidingScriptName(scriptRequest.location, engine.groovyClassLoader, logger)
                script = engine.createScript(scriptRequest.location, binding)
            }
            assert script instanceof JobParent

            JobParent jobParent = (JobParent) script
            jobParent.setJm(jobManagement)

            binding.setVariable('jobFactory', jobParent)

            script.run()

            return jobParent
        } catch (CompilationFailedException e) {
            throw new DslException(e.message, e)
        } catch (GroovyRuntimeException e) {
            throw new DslScriptException(e.message, e)
        } catch (ResourceException e) {
            throw new IOException('Unable to run script', e)
        } catch (ScriptException e) {
            throw new IOException('Unable to run script', e)
        }
    }

    private static boolean isValidScriptName(String scriptFile) {
        String normalizedName = getScriptName(scriptFile)
        if (normalizedName.length() == 0 || !Character.isJavaIdentifierStart(normalizedName.charAt(0))) {
            return false
        }
        for (int i = 1; i < normalizedName.length(); i += 1) {
            if (!Character.isJavaIdentifierPart(normalizedName.charAt(i))) {
                return false
            }
        }
        true
    }

    private static void checkValidScriptName(String scriptName) {
        if (!isValidScriptName(scriptName)) {
            throw new DslException(
                "invalid script name '${scriptName}; script names may only contain " +
                    'letters, digits and underscores, but may not start with a digit'
            )
        }
    }

    private static void checkCollidingScriptName(String scriptFile, ClassLoader classLoader, PrintStream logger) {
        String scriptName = getScriptName(scriptFile)
        Package[] packages = new SnitchingClassLoader(classLoader).packages
        if (packages.any { it.name == scriptName || it.name.startsWith("${scriptName}.") }) {
            logger.println(
                    "Warning: the script name '${scriptFile} is identical to a package name; choose a different " +
                            'script name to avoid problems'
            )
        }
    }

    private static String getScriptName(String scriptFile) {
        String fileName = new File(scriptFile).name
        int idx = fileName.lastIndexOf('.')
        idx > -1 ? fileName[0..idx - 1] : fileName
    }

    private static Set<GeneratedJob> extractGeneratedJobs(JobParent jobParent,
                                                          boolean ignoreExisting) throws IOException {
        // Iterate jobs which were setup, save them, and convert to a serializable form
        Set<GeneratedJob> generatedJobs = new LinkedHashSet<GeneratedJob>()
        if (jobParent != null) {
            List<Item> referencedItems = new ArrayList<Item>(jobParent.referencedJobs) // As List
            Collections.sort(referencedItems, ITEM_COMPARATOR)
            referencedItems.each { Item item ->
                String xml = item.xml
                LOGGER.log(Level.FINE, "Saving item ${item.name} as ${xml}")
                if (item instanceof Job) {
                    Job job = (Job) item
                    if (job.previousNamesRegex != null) {
                        jobParent.jm.renameJobMatching(job.previousNamesRegex, job.name)
                    }
                }
                jobParent.jm.createOrUpdateConfig(item, ignoreExisting)
                String templateName = item instanceof Job ? ((Job) item).templateName : null
                generatedJobs << new GeneratedJob(templateName, item.name)
            }
        }
        generatedJobs
    }

    private static Set<GeneratedView> extractGeneratedViews(JobParent jobParent, boolean ignoreExisting) {
        Set<GeneratedView> generatedViews = new LinkedHashSet<GeneratedView>()
        jobParent.referencedViews.each { View view ->
            String xml = view.xml
            LOGGER.log(Level.FINE, "Saving view ${view.name} as ${xml}")
            jobParent.jm.createOrUpdateView(view.name, xml, ignoreExisting)
            generatedViews << new GeneratedView(view.name)
        }
        generatedViews
    }

    private static Set<GeneratedConfigFile> extractGeneratedConfigFiles(JobParent jobParent, boolean ignoreExisting) {
        Set<GeneratedConfigFile> generatedConfigFiles = new LinkedHashSet<GeneratedConfigFile>()
        jobParent.referencedConfigFiles.each { ConfigFile configFile ->
            LOGGER.log(Level.FINE, "Saving config file ${configFile.name}")
            String id = jobParent.jm.createOrUpdateConfigFile(configFile, ignoreExisting)
            generatedConfigFiles << new GeneratedConfigFile(id, configFile.name)
        }
        generatedConfigFiles
    }

    private static Set<GeneratedUserContent> extractGeneratedUserContents(JobParent jobParent, boolean ignoreExisting) {
        Set<GeneratedUserContent> generatedUserContents = new LinkedHashSet<GeneratedUserContent>()
        jobParent.referencedUserContents.each { UserContent userContent ->
            LOGGER.log(Level.FINE, "Saving user content ${userContent.path}")
            jobParent.jm.createOrUpdateUserContent(userContent, ignoreExisting)
            generatedUserContents << new GeneratedUserContent(userContent.path)
        }
        generatedUserContents
    }

    @SuppressWarnings('CatchException')
    private void scheduleJobsToRun(List<String> jobNames) {
        Map<String, Throwable> exceptions = [:]
        jobNames.each { String jobName ->
            try {
                jobManagement.queueJob(jobName)
            } catch (Exception e) {
                exceptions[jobName] = e
            }
        }
        if (!exceptions.isEmpty()) {
            LOGGER.warning('Trouble schedule some jobs')
            exceptions.each { String jobName, Throwable exception ->
                LOGGER.throwing('DslScriptLoader', jobName, exception)
            }
        }
    }

    private Binding createBinding() {
        Binding binding = new Binding()
        binding.setVariable('out', jobManagement.outputStream) // Works for println, but not System.out

        jobManagement.parameters.each { String key, String value ->
            LOGGER.fine("Binding ${key} to ${value}")
            binding.setVariable(key, value)
        }
        binding
    }

    private static CompilerConfiguration createCompilerConfiguration(JobManagement jobManagement) {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT)
        config.scriptBaseClass = 'javaposse.jobdsl.dsl.JobParent'

        // Import some of our helper classes so that user doesn't have to.
        ImportCustomizer icz = new ImportCustomizer()
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.ArchiveXUnitContext.ThresholdMode')
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior')
        icz.addImports('javaposse.jobdsl.dsl.helpers.step.condition.FileExistsCondition.BaseDir')
        icz.addImports('javaposse.jobdsl.dsl.views.ListView.StatusFilter')
        icz.addImports('javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle')
        icz.addImports('javaposse.jobdsl.dsl.views.DeliveryPipelineView.Sorting')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.Status')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.MatchType')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.RegexMatchValue')
        icz.addImports('javaposse.jobdsl.dsl.helpers.scm.SvnCheckoutStrategy')
        icz.addImports('javaposse.jobdsl.dsl.helpers.scm.SvnDepth')
        icz.addImports('javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation')
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes')
        config.addCompilationCustomizers(icz)

        config.output = new PrintWriter(jobManagement.outputStream) // This seems to do nothing
        config
    }

    private static class SnitchingClassLoader extends ClassLoader {
        SnitchingClassLoader(ClassLoader parent) {
            super(parent)
        }

        @Override
        Package[] getPackages() {
            super.packages
        }
    }
}
