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
     * Creates a new {@link DslScriptLoader} which will use the given {@link JobManagement} instance.
     *
     * @since 1.45
     */
    DslScriptLoader(JobManagement jobManagement) {
        this.jobManagement = jobManagement
        this.logger = jobManagement.outputStream
    }

    /**
     * Executes the script requests and returns the generated items.
     *
     * @since 1.45
     */
    GeneratedItems runScripts(Collection<ScriptRequest> scriptRequests) throws IOException {
        ClassLoader parentClassLoader = DslScriptLoader.classLoader
        CompilerConfiguration config = createCompilerConfiguration()

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

    /**
     * Executes the script and returns the generated items.
     *
     * @since 1.47
     */
    GeneratedItems runScript(String script) throws IOException {
        runScripts([new ScriptRequest(script)])
    }

    private GeneratedItems runScriptsWithClassLoader(Collection<ScriptRequest> scriptRequests,
                                                     GroovyClassLoader groovyClassLoader,
                                                     CompilerConfiguration config) {
        GeneratedItems generatedItems = new GeneratedItems()
        Map<String, GroovyScriptEngine> engineCache = [:]

        try {
            scriptRequests.each { ScriptRequest scriptRequest ->
                String key = scriptRequest.urlRoots*.toString().sort().join(',')

                GroovyScriptEngine engine = engineCache[key]
                if (!engine) {
                    engine = new GroovyScriptEngine(scriptRequest.urlRoots, groovyClassLoader)
                    engine.config = config
                    engineCache[key] = engine
                }

                JobParent jobParent = runScriptEngine(scriptRequest, engine)

                generatedItems.configFiles.addAll(
                        extractGeneratedConfigFiles(jobParent.referencedConfigFiles, scriptRequest.ignoreExisting)
                )
                generatedItems.jobs.addAll(
                        extractGeneratedJobs(jobParent.referencedJobs, scriptRequest.ignoreExisting)
                )
                generatedItems.views.addAll(
                        extractGeneratedViews(jobParent.referencedViews, scriptRequest.ignoreExisting)
                )
                generatedItems.userContents.addAll(
                        extractGeneratedUserContents(jobParent.referencedUserContents, scriptRequest.ignoreExisting)
                )

                scheduleJobsToRun(jobParent.queueToBuild)
            }
        } finally {
            engineCache.values().each { GroovyScriptEngine engine ->
                if (engine?.groovyClassLoader instanceof Closeable) {
                    ((Closeable) engine.groovyClassLoader).close()
                }
            }
        }

        generatedItems
    }

    private JobParent runScriptEngine(ScriptRequest scriptRequest, GroovyScriptEngine engine) {
        LOGGER.log(Level.FINE, String.format("Request for ${scriptRequest.location}"))

        Binding binding = createBinding(scriptRequest)
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

    /**
     * @Deprecated use {@link #runScripts(java.util.Collection)}
     */
    @Deprecated
    static GeneratedItems runDslEngine(String scriptBody, JobManagement jobManagement) throws IOException {
        DslScriptLoader loader = new DslScriptLoader(jobManagement)
        loader.runScript(scriptBody)
    }

    /**
     * @Deprecated use {@link #runScripts(java.util.Collection)}
     */
    @Deprecated
    static GeneratedItems runDslEngine(ScriptRequest scriptRequest, JobManagement jobManagement) throws IOException {
        DslScriptLoader loader = new DslScriptLoader(jobManagement)
        loader.runScripts([scriptRequest])
    }

    private static String getScriptName(String scriptFile) {
        String fileName = new File(scriptFile).name
        int idx = fileName.lastIndexOf('.')
        idx > -1 ? fileName[0..idx - 1] : fileName
    }

    private Set<GeneratedJob> extractGeneratedJobs(Set<Item> referencedItems,
                                                   boolean ignoreExisting) throws IOException {
        Set<GeneratedJob> generatedJobs = new LinkedHashSet<GeneratedJob>()
        referencedItems.sort(false, ITEM_COMPARATOR).each { Item item ->
            String xml = item.xml
            LOGGER.log(Level.FINE, "Saving item ${item.name} as ${xml}")
            if (item instanceof Job) {
                Job job = (Job) item
                if (job.previousNamesRegex != null) {
                    jobManagement.renameJobMatching(job.previousNamesRegex, job.name)
                }
            }
            jobManagement.createOrUpdateConfig(item, ignoreExisting)
            String templateName = item instanceof Job ? ((Job) item).templateName : null
            generatedJobs << new GeneratedJob(templateName, item.name)
        }
        generatedJobs
    }

    private Set<GeneratedView> extractGeneratedViews(Set<View> referencedViews, boolean ignoreExisting) {
        Set<GeneratedView> generatedViews = new LinkedHashSet<GeneratedView>()
        referencedViews.each { View view ->
            String xml = view.xml
            LOGGER.log(Level.FINE, "Saving view ${view.name} as ${xml}")
            jobManagement.createOrUpdateView(view.name, xml, ignoreExisting)
            generatedViews << new GeneratedView(view.name)
        }
        generatedViews
    }

    private Set<GeneratedConfigFile> extractGeneratedConfigFiles(Set<ConfigFile> referencedConfigFiles,
                                                                 boolean ignoreExisting) {
        Set<GeneratedConfigFile> generatedConfigFiles = new LinkedHashSet<GeneratedConfigFile>()
        referencedConfigFiles.each { ConfigFile configFile ->
            LOGGER.log(Level.FINE, "Saving config file ${configFile.name}")
            String id = jobManagement.createOrUpdateConfigFile(configFile, ignoreExisting)
            generatedConfigFiles << new GeneratedConfigFile(id, configFile.name)
        }
        generatedConfigFiles
    }

    private Set<GeneratedUserContent> extractGeneratedUserContents(Set<UserContent> referencedUserContents,
                                                                   boolean ignoreExisting) {
        Set<GeneratedUserContent> generatedUserContents = new LinkedHashSet<GeneratedUserContent>()
        referencedUserContents.each { UserContent userContent ->
            LOGGER.log(Level.FINE, "Saving user content ${userContent.path}")
            jobManagement.createOrUpdateUserContent(userContent, ignoreExisting)
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

    private Binding createBinding(ScriptRequest scriptRequest) {
        Binding binding = new Binding()
        binding.setVariable('out', jobManagement.outputStream) // Works for println, but not System.out
        if (scriptRequest.scriptPath) {
            binding.setVariable('__FILE__', scriptRequest.scriptPath)
        }

        jobManagement.parameters.each { String key, String value ->
            LOGGER.fine("Binding ${key} to ${value}")
            binding.setVariable(key, value)
        }
        binding
    }

    private CompilerConfiguration createCompilerConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT)
        config.scriptBaseClass = 'javaposse.jobdsl.dsl.JobParent'

        // Import some of our helper classes so that user doesn't have to.
        ImportCustomizer icz = new ImportCustomizer()
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.ArchiveXUnitContext.ThresholdMode')
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.PublisherContext.Behavior')
        icz.addImports('javaposse.jobdsl.dsl.helpers.step.RunConditionContext.BaseDir')
        icz.addImports('javaposse.jobdsl.dsl.views.ListView.StatusFilter')
        icz.addImports('javaposse.jobdsl.dsl.views.BuildPipelineView.OutputStyle')
        icz.addImports('javaposse.jobdsl.dsl.views.DeliveryPipelineView.Sorting')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.AmountType')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.BuildCountType')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.BuildStatusType')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.Status')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.MatchType')
        icz.addImports('javaposse.jobdsl.dsl.views.jobfilter.RegexMatchValue')
        icz.addImports('javaposse.jobdsl.dsl.views.portlets.TestTrendChartContext.DisplayStatus')
        icz.addImports('javaposse.jobdsl.dsl.helpers.scm.SvnCheckoutStrategy')
        icz.addImports('javaposse.jobdsl.dsl.helpers.scm.SvnDepth')
        icz.addImports('javaposse.jobdsl.dsl.helpers.scm.GitMergeOptionsContext.FastForwardMergeMode')
        icz.addImports('javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation')
        icz.addImports('javaposse.jobdsl.dsl.helpers.publisher.WeblogicDeployerContext.WeblogicDeploymentStageModes')
        icz.addImports('javaposse.jobdsl.dsl.helpers.triggers.BuildResultTriggerContext.BuildResult')
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
