package javaposse.jobdsl.dsl

import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import java.util.logging.Level
import java.util.logging.Logger

import static groovy.lang.GroovyShell.DEFAULT_CODE_BASE

/**
 * Runs provided DSL scripts via an external {@link JobManagement}.
 */
abstract class AbstractDslScriptLoader<S extends JobParent, G extends GeneratedItems> {
    private static final Logger LOGGER = Logger.getLogger(AbstractDslScriptLoader.name)
    private static final Comparator<? super Item> ITEM_COMPARATOR = new ItemProcessingOrderComparator()

    protected final JobManagement jobManagement
    protected final Class<S> scriptBaseClass
    protected final Class<G> generatedItemsClass
    private final PrintStream logger

    /**
     * @since 1.58
     */
    protected AbstractDslScriptLoader(JobManagement jobManagement, Class<S> scriptBaseClass,
                                      Class<G> generatedItemsClass) {
        this.jobManagement = jobManagement
        this.logger = jobManagement.outputStream
        this.scriptBaseClass = scriptBaseClass
        this.generatedItemsClass = generatedItemsClass
    }

    /**
     * Executes the script requests and returns the generated items.
     *
     * @since 1.45
     */
    G runScripts(Collection<ScriptRequest> scriptRequests,
                 Map<String, Object> additionalParameters = null) throws IOException {
        G generatedItems = generatedItemsClass.newInstance()
        CompilerConfiguration config = createCompilerConfiguration()
        customizeCompilerConfiguration(config)
        Map<String, GroovyShell> groovyShellCache = [:]
        try {
            scriptRequests.each { ScriptRequest scriptRequest ->
                String key = scriptRequest.urlRoots*.toString().sort().join(',')

                GroovyShell groovyShell = groovyShellCache[key]
                if (!groovyShell) {
                    ClassLoader classLoader = prepareClassLoader(AbstractDslScriptLoader.classLoader)
                    groovyShell = new GroovyShell(
                            new URLClassLoader(scriptRequest.urlRoots, classLoader),
                            new Binding(),
                            config
                    )
                    groovyShellCache[key] = groovyShell
                }

                S jobParent = runScriptEngine(scriptRequest, groovyShell, additionalParameters)

                extractGeneratedItems(generatedItems, jobParent, scriptRequest)

                scheduleJobsToRun(jobParent.queueToBuild)
            }
        } finally {
            groovyShellCache.values().each { GroovyShell groovyShell ->
                if (groovyShell.classLoader instanceof Closeable) {
                    ((Closeable) groovyShell.classLoader).close()
                }
                if (groovyShell.classLoader.parent instanceof Closeable) {
                    ((Closeable) groovyShell.classLoader.parent).close()
                }
            }
        }
        generatedItems
    }

    /**
     * Executes the script and returns the generated items.
     *
     * @since 1.47
     */
    G runScript(String script, Map<String, Object> additionalParameters = null) throws IOException {
        runScripts([new ScriptRequest(script)], additionalParameters)
    }

    protected S runScriptEngine(ScriptRequest scriptRequest, GroovyShell groovyShell,
                                Map<String, Object> additionalParameters) {
        try {
            if (scriptRequest.scriptPath || scriptRequest.location) {
                logger.println("Processing DSL script ${scriptRequest.scriptName}")
                checkValidScriptName(scriptRequest)
                checkCollidingScriptName(scriptRequest, groovyShell.classLoader, logger)
            } else {
                logger.println('Processing provided DSL script')
            }

            Script script = groovyShell.parse(createGroovyCodeSource(scriptRequest))
            script.binding = createBinding(scriptRequest, additionalParameters)
            script.binding.setVariable('jobFactory', script)

            S jobParent = (S) script
            jobParent.setJm(jobManagement)

            runScript(script)

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

    protected ClassLoader prepareClassLoader(ClassLoader classLoader) {
        classLoader
    }

    protected GroovyCodeSource createGroovyCodeSource(ScriptRequest scriptRequest) {
        if (scriptRequest.body != null) {
            new GroovyCodeSource(scriptRequest.body, scriptRequest.scriptName ?: 'script', DEFAULT_CODE_BASE)
        } else {
            new GroovyCodeSource(new URL(scriptRequest.urlRoots[0], scriptRequest.location))
        }
    }

    protected void runScript(Script script) {
        script.run()
    }

    private static boolean isValidScriptName(ScriptRequest scriptRequest) {
        String normalizedName = scriptRequest.scriptBaseName
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

    private static void checkValidScriptName(ScriptRequest scriptRequest) {
        if (!isValidScriptName(scriptRequest)) {
            throw new DslException(
                "invalid script name '${scriptRequest.scriptName}; script names may only contain " +
                    'letters, digits and underscores, but may not start with a digit'
            )
        }
    }

    private static void checkCollidingScriptName(ScriptRequest scriptRequest, ClassLoader classLoader,
                                                 PrintStream logger) {
        String scriptName = scriptRequest.scriptBaseName
        Package[] packages = new SnitchingClassLoader(classLoader).packages
        if (packages.any { it.name == scriptName || it.name.startsWith("${scriptName}.") }) {
            logger.println(
                    "Warning: the script name '${scriptRequest.scriptName} is identical to a package name; choose a " +
                            'different script name to avoid problems'
            )
        }
    }

    /**
     * @since 1.58
     */
    protected void extractGeneratedItems(G generatedItems, S jobParent, ScriptRequest scriptRequest) {
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
    }

    protected Set<GeneratedJob> extractGeneratedJobs(Set<Item> referencedItems,
                                                     boolean ignoreExisting) throws IOException {
        Set<GeneratedJob> generatedJobs = new LinkedHashSet<GeneratedJob>()
        referencedItems.sort(false, ITEM_COMPARATOR).each { Item item ->
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

    protected Set<GeneratedView> extractGeneratedViews(Set<View> referencedViews, boolean ignoreExisting) {
        Set<GeneratedView> generatedViews = new LinkedHashSet<GeneratedView>()
        referencedViews.each { View view ->
            String xml = view.xml
            LOGGER.log(Level.FINE, "Saving view ${view.name} as ${xml}")
            jobManagement.createOrUpdateView(view.name, xml, ignoreExisting)
            generatedViews << new GeneratedView(view.name)
        }
        generatedViews
    }

    protected Set<GeneratedConfigFile> extractGeneratedConfigFiles(Set<ConfigFile> referencedConfigFiles,
                                                                   boolean ignoreExisting) {
        Set<GeneratedConfigFile> generatedConfigFiles = new LinkedHashSet<GeneratedConfigFile>()
        referencedConfigFiles.each { ConfigFile configFile ->
            LOGGER.log(Level.FINE, "Saving config file ${configFile.name}")
            String id = jobManagement.createOrUpdateConfigFile(configFile, ignoreExisting)
            generatedConfigFiles << new GeneratedConfigFile(id, configFile.name)
        }
        generatedConfigFiles
    }

    protected Set<GeneratedUserContent> extractGeneratedUserContents(Set<UserContent> referencedUserContents,
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
    protected void scheduleJobsToRun(List<String> jobNames) {
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

    private Binding createBinding(ScriptRequest scriptRequest, Map<String, Object> additionalParameters) {
        Binding binding = new Binding()
        binding.setVariable('out', jobManagement.outputStream) // Works for println, but not System.out
        if (scriptRequest.scriptPath) {
            binding.setVariable('__FILE__', scriptRequest.scriptPath)
        }

        LOGGER.fine('Binding job parameters')
        jobManagement.parameters.each { String key, Object value ->
            LOGGER.fine("Binding ${key} to ${value}")
            binding.setVariable(key, value)
        }

        LOGGER.fine('Binding additional parameters')
        additionalParameters.each { String parameterName, Object value ->
            if (binding.hasVariable(parameterName)) {
                LOGGER.fine("Rebinding ${parameterName} to ${value}")
            } else {
                LOGGER.fine("Binding ${parameterName} to ${value}")
            }
            binding.setVariable(parameterName, value)
        }

        binding
    }

    protected CompilerConfiguration createCompilerConfiguration() {
        new CompilerConfiguration(CompilerConfiguration.DEFAULT)
    }

    private void customizeCompilerConfiguration(CompilerConfiguration config) {
        config.scriptBaseClass = scriptBaseClass.name

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
