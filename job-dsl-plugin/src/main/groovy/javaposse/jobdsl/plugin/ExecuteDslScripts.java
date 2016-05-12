package javaposse.jobdsl.plugin;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Items;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.tasks.Builder;
import javaposse.jobdsl.dsl.DslException;
import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedConfigFile;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.GeneratedUserContent;
import javaposse.jobdsl.dsl.GeneratedView;
import javaposse.jobdsl.dsl.JobManagement;
import javaposse.jobdsl.dsl.ScriptRequest;
import javaposse.jobdsl.plugin.actions.ApiViewerAction;
import javaposse.jobdsl.plugin.actions.GeneratedConfigFilesAction;
import javaposse.jobdsl.plugin.actions.GeneratedConfigFilesBuildAction;
import javaposse.jobdsl.plugin.actions.GeneratedJobsAction;
import javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction;
import javaposse.jobdsl.plugin.actions.GeneratedUserContentsAction;
import javaposse.jobdsl.plugin.actions.GeneratedUserContentsBuildAction;
import javaposse.jobdsl.plugin.actions.GeneratedViewsAction;
import javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction;
import jenkins.model.Jenkins;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static javaposse.jobdsl.plugin.actions.GeneratedObjectsAction.extractGeneratedObjects;

/**
 * This Builder keeps a list of job DSL scripts, and when prompted, executes these to create /
 * update Jenkins jobs.
 */
public class ExecuteDslScripts extends Builder {
    private static final Logger LOGGER = Logger.getLogger(ExecuteDslScripts.class.getName());

    // Artifact of how Jelly/Stapler puts conditional variables in blocks, which NEED to map to a sub-Object.
    // The alternative would have been to mess with DescriptorImpl.getInstance
    public static class ScriptLocation {
        @DataBoundConstructor
        public ScriptLocation(String value, String targets, String scriptText) {
            this.usingScriptText = value == null || Boolean.parseBoolean(value);
            this.targets = Util.fixEmptyAndTrim(targets);
            this.scriptText = Util.fixEmptyAndTrim(scriptText);
        }

        private final boolean usingScriptText;
        private final String targets;
        private final String scriptText;
    }

    /**
     * Newline-separated list of locations to load as dsl scripts.
     */
    private final String targets;

    /**
     * Text of a dsl script.
     */
    private final String scriptText;

    /**
     * Whether we're using some text for the script directly
     */
    private final boolean usingScriptText;

    private final boolean ignoreExisting;

    private final RemovedJobAction removedJobAction;

    private final RemovedViewAction removedViewAction;

    private final LookupStrategy lookupStrategy;

    private final String additionalClasspath;

    @DataBoundConstructor
    public ExecuteDslScripts(ScriptLocation scriptLocation, boolean ignoreExisting, RemovedJobAction removedJobAction,
                             RemovedViewAction removedViewAction, LookupStrategy lookupStrategy,
                             String additionalClasspath) {
        // Copy over from embedded object
        this.usingScriptText = scriptLocation == null || scriptLocation.usingScriptText;
        this.targets = scriptLocation == null ? null : scriptLocation.targets;
        this.scriptText = scriptLocation == null ? null : scriptLocation.scriptText;
        this.ignoreExisting = ignoreExisting;
        this.removedJobAction = removedJobAction;
        this.removedViewAction = removedViewAction;
        this.lookupStrategy = lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
        this.additionalClasspath = additionalClasspath;
    }

    public ExecuteDslScripts(ScriptLocation scriptLocation, boolean ignoreExisting, RemovedJobAction removedJobAction,
                             LookupStrategy lookupStrategy) {
        this(scriptLocation, ignoreExisting, removedJobAction, RemovedViewAction.IGNORE, lookupStrategy, null);
    }

    public ExecuteDslScripts(ScriptLocation scriptLocation, boolean ignoreExisting, RemovedJobAction removedJobAction) {
        this(scriptLocation, ignoreExisting, removedJobAction, LookupStrategy.JENKINS_ROOT);
    }

    public ExecuteDslScripts(ScriptLocation scriptLocation, boolean ignoreExisting, RemovedJobAction removedJobAction,
                             RemovedViewAction removedViewAction, LookupStrategy lookupStrategy) {
        this(scriptLocation, ignoreExisting, removedJobAction, removedViewAction, lookupStrategy, null);
    }

    ExecuteDslScripts(String scriptText) {
        this.usingScriptText = true;
        this.scriptText = scriptText;
        this.targets = null;
        this.ignoreExisting = false;
        this.removedJobAction = RemovedJobAction.DISABLE;
        this.removedViewAction = RemovedViewAction.IGNORE;
        this.lookupStrategy = LookupStrategy.JENKINS_ROOT;
        this.additionalClasspath = null;
    }

    ExecuteDslScripts() {
        this(null);
    }

    public String getTargets() {
        return targets;
    }

    public String getScriptText() {
        return scriptText;
    }

    public boolean isUsingScriptText() {
        return usingScriptText;
    }

    public boolean isIgnoreExisting() {
        return ignoreExisting;
    }

    public RemovedJobAction getRemovedJobAction() {
        return removedJobAction;
    }

    public RemovedViewAction getRemovedViewAction() {
        return removedViewAction;
    }

    public LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public String getAdditionalClasspath() {
        return additionalClasspath;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        return Arrays.<Action>asList(
                new GeneratedJobsAction(project),
                new GeneratedViewsAction(project),
                new GeneratedConfigFilesAction(project),
                new GeneratedUserContentsAction(project),
                new ApiViewerAction()
        );
    }

    /**
     * Runs every job DSL script provided in the plugin configuration, which results in new /
     * updated Jenkins jobs. The created / updated jobs are reported in the build result.
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher,
                           final BuildListener listener) throws InterruptedException, IOException {
        try {
            EnvVars env = build.getEnvironment(listener);
            env.putAll(build.getBuildVariables());

            JobManagement jm = new InterruptibleJobManagement(
                    new JenkinsJobManagement(listener.getLogger(), env, build, getLookupStrategy())
            );

            ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env);
            try {
                Set<ScriptRequest> scriptRequests = generator.getScriptRequests(
                        targets, usingScriptText, scriptText, ignoreExisting, additionalClasspath
                );

                DslScriptLoader dslScriptLoader = new DslScriptLoader(jm);
                GeneratedItems generatedItems = dslScriptLoader.runScripts(scriptRequests);
                Set<GeneratedJob> freshJobs = generatedItems.getJobs();
                Set<GeneratedView> freshViews = generatedItems.getViews();
                Set<GeneratedConfigFile> freshConfigFiles = generatedItems.getConfigFiles();
                Set<GeneratedUserContent> freshUserContents = generatedItems.getUserContents();

                updateTemplates(build, listener, freshJobs);
                updateGeneratedJobs(build, listener, freshJobs);
                updateGeneratedViews(build, listener, freshViews);
                updateGeneratedConfigFiles(build, listener, freshConfigFiles);
                updateGeneratedUserContents(build, listener, freshUserContents);

                // Save onto Builder, which belongs to a Project.
                build.addAction(new GeneratedJobsBuildAction(freshJobs, getLookupStrategy()));
                build.addAction(new GeneratedViewsBuildAction(freshViews, getLookupStrategy()));
                build.addAction(new GeneratedConfigFilesBuildAction(freshConfigFiles));
                build.addAction(new GeneratedUserContentsBuildAction(freshUserContents));

                return true;
            } finally {
                generator.close();
            }
        } catch (RuntimeException e) {
            if (!(e instanceof DslException)) {
                e.printStackTrace(listener.getLogger());
            }
            LOGGER.log(Level.FINE, String.format("Exception while processing DSL scripts: %s", e.getMessage()), e);
            throw new AbortException(e.getMessage());
        }
    }


    /**
     * Uses generatedJobs as existing data, so call before updating generatedJobs.
     */
    private Set<String> updateTemplates(AbstractBuild<?, ?> build, BuildListener listener,
                                        Set<GeneratedJob> freshJobs) throws IOException {
        AbstractProject<?, ?> seedJob = build.getProject();

        Set<String> freshTemplates = getTemplates(freshJobs);
        Set<String> existingTemplates = getTemplates(extractGeneratedObjects(seedJob, GeneratedJobsAction.class));
        Set<String> newTemplates = Sets.difference(freshTemplates, existingTemplates);
        Set<String> removedTemplates = Sets.difference(existingTemplates, freshTemplates);

        logItems(listener, "Existing templates", existingTemplates);
        logItems(listener, "New templates", newTemplates);
        logItems(listener, "Unreferenced templates", removedTemplates);

        // Collect information about the templates we loaded
        final String seedJobName = seedJob.getName();
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        boolean descriptorMutated = false;

        // Clean up
        for (String templateName : removedTemplates) {
            Collection<SeedReference> seedJobReferences = descriptor.getTemplateJobMap().get(templateName);
            Collection<SeedReference> matching = Collections2.filter(seedJobReferences, new SeedNamePredicate(seedJobName));
            if (!matching.isEmpty()) {
                seedJobReferences.removeAll(matching);
                descriptorMutated = true;
            }
        }

        // Ensure we have a reference
        for (String templateName : freshTemplates) {
            Collection<SeedReference> seedJobReferences = descriptor.getTemplateJobMap().get(templateName);
            Collection<SeedReference> matching = Collections2.filter(seedJobReferences, new SeedNamePredicate(seedJobName));

            AbstractProject templateProject = getLookupStrategy().getItem(seedJob, templateName, AbstractProject.class);
            final String digest = Util.getDigestOf(new FileInputStream(templateProject.getConfigFile().getFile()));

            if (matching.size() == 1) {
                // Just update digest
                SeedReference ref = Iterables.get(matching, 0);
                if (digest.equals(ref.getDigest())) {
                    ref.setDigest(digest);
                    descriptorMutated = true;
                }
            } else {
                if (matching.size() > 1) {
                    // Not sure how there could be more one, throw it all away and start over
                    seedJobReferences.removeAll(matching);
                }
                seedJobReferences.add(new SeedReference(templateName, seedJobName, digest));
                descriptorMutated = true;
            }
        }

        if (descriptorMutated) {
            descriptor.save();
        }
        return freshTemplates;
    }


    private void updateGeneratedJobs(final AbstractBuild<?, ?> build, BuildListener listener,
                                     Set<GeneratedJob> freshJobs) throws IOException, InterruptedException {
        // Update Project
        Set<GeneratedJob> generatedJobs = extractGeneratedObjects(build.getProject(), GeneratedJobsAction.class);
        Set<GeneratedJob> added = Sets.difference(freshJobs, generatedJobs);
        Set<GeneratedJob> existing = Sets.intersection(generatedJobs, freshJobs);
        Set<GeneratedJob> unreferenced = Sets.difference(generatedJobs, freshJobs);
        Set<GeneratedJob> removed = new HashSet<GeneratedJob>();
        Set<GeneratedJob> disabled = new HashSet<GeneratedJob>();

        logItems(listener, "Added items", added);
        logItems(listener, "Existing items", existing);
        logItems(listener, "Unreferenced items", unreferenced);

        // Update unreferenced jobs
        for (GeneratedJob unreferencedJob : unreferenced) {
            Item removedItem = getLookupStrategy().getItem(build.getProject(), unreferencedJob.getJobName(), Item.class);
            if (removedItem != null && removedJobAction != RemovedJobAction.IGNORE) {
                if (removedJobAction == RemovedJobAction.DELETE) {
                    removedItem.delete();
                    removed.add(unreferencedJob);
                } else {
                    if (removedItem instanceof AbstractProject) {
                        ((AbstractProject) removedItem).disable();
                        disabled.add(unreferencedJob);
                    }
                }
            }
        }

        // print what happened with unreferenced jobs
        logItems(listener, "Disabled items", disabled);
        logItems(listener, "Removed items", removed);

        updateGeneratedJobMap(build.getProject(), Sets.union(added, existing), unreferenced);
    }

    private void updateGeneratedJobMap(AbstractProject<?, ?> seedJob, Set<GeneratedJob> createdOrUpdatedJobs,
                                       Set<GeneratedJob> removedJobs) throws IOException {
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        boolean descriptorMutated = false;
        Map<String, SeedReference> generatedJobMap = descriptor.getGeneratedJobMap();

        for (GeneratedJob generatedJob : createdOrUpdatedJobs) {
            Item item = getLookupStrategy().getItem(seedJob, generatedJob.getJobName(), Item.class);
            if (item != null) {
                SeedReference newSeedReference = new SeedReference(seedJob.getFullName());
                if (generatedJob.getTemplateName() != null) {
                    Item template = getLookupStrategy().getItem(seedJob, generatedJob.getTemplateName(), Item.class);
                    if (template != null) {
                        newSeedReference.setTemplateJobName(template.getFullName());
                    }
                }
                newSeedReference.setDigest(Util.getDigestOf(Items.getConfigFile(item).getFile()));

                SeedReference oldSeedReference = generatedJobMap.get(item.getFullName());
                if (!newSeedReference.equals(oldSeedReference)) {
                    generatedJobMap.put(item.getFullName(), newSeedReference);
                    descriptorMutated = true;
                }
            }
        }

        for (GeneratedJob removedJob : removedJobs) {
            Item removedItem = getLookupStrategy().getItem(seedJob, removedJob.getJobName(), Item.class);
            if (removedItem != null) {
                generatedJobMap.remove(removedItem.getFullName());
                descriptorMutated = true;
            }
        }

        if (descriptorMutated) {
            descriptor.save();
        }
    }

    private void updateGeneratedViews(AbstractBuild<?, ?> build, BuildListener listener,
                                      Set<GeneratedView> freshViews) throws IOException {
        Set<GeneratedView> generatedViews = extractGeneratedObjects(build.getProject(), GeneratedViewsAction.class);
        Set<GeneratedView> added = Sets.difference(freshViews, generatedViews);
        Set<GeneratedView> existing = Sets.intersection(generatedViews, freshViews);
        Set<GeneratedView> unreferenced = Sets.difference(generatedViews, freshViews);
        Set<GeneratedView> removed = new HashSet<GeneratedView>();

        logItems(listener, "Added views", added);
        logItems(listener, "Existing views", existing);
        logItems(listener, "Unreferenced views", unreferenced);

        // Delete views
        if (removedViewAction == RemovedViewAction.DELETE) {
            for (GeneratedView unreferencedView : unreferenced) {
                String viewName = unreferencedView.getName();
                ItemGroup parent = getLookupStrategy().getParent(build.getProject(), viewName);
                if (parent instanceof ViewGroup) {
                    View view = ((ViewGroup) parent).getView(FilenameUtils.getName(viewName));
                    if (view != null) {
                        ((ViewGroup) parent).deleteView(view);
                        removed.add(unreferencedView);
                    }
                } else if (parent == null) {
                    LOGGER.log(Level.FINE, "Parent ViewGroup seems to have been already deleted");
                } else {
                    LOGGER.log(Level.WARNING, format("Could not delete view within %s", parent.getClass()));
                }
            }
        }

        logItems(listener, "Removed views", removed);
    }

    private void updateGeneratedConfigFiles(AbstractBuild<?, ?> build, BuildListener listener,
                                            Set<GeneratedConfigFile> freshConfigFiles) {
        Set<GeneratedConfigFile> generatedConfigFiles = extractGeneratedObjects(build.getProject(), GeneratedConfigFilesAction.class);
        Set<GeneratedConfigFile> added = Sets.difference(freshConfigFiles, generatedConfigFiles);
        Set<GeneratedConfigFile> existing = Sets.intersection(generatedConfigFiles, freshConfigFiles);
        Set<GeneratedConfigFile> unreferenced = Sets.difference(generatedConfigFiles, freshConfigFiles);

        logItems(listener, "Added config files", added);
        logItems(listener, "Existing config files", existing);
        logItems(listener, "Unreferenced config files", unreferenced);
    }

    private void updateGeneratedUserContents(AbstractBuild<?, ?> build, BuildListener listener,
                                             Set<GeneratedUserContent> freshUserContents) {
        Set<GeneratedUserContent> generatedUserContents = extractGeneratedObjects(build.getProject(), GeneratedUserContentsAction.class);
        Set<GeneratedUserContent> added = Sets.difference(freshUserContents, generatedUserContents);
        Set<GeneratedUserContent> existing = Sets.intersection(generatedUserContents, freshUserContents);
        Set<GeneratedUserContent> unreferenced = Sets.difference(generatedUserContents, freshUserContents);

        logItems(listener, "Adding user content", added);
        logItems(listener, "Existing user content", existing);
        logItems(listener, "Unreferenced user content", unreferenced);
    }

    private static void logItems(BuildListener listener, String message, Collection<?> collection) {
        if (!collection.isEmpty()) {
            listener.getLogger().println(message + ":");
            for (Object item : collection) {
                listener.getLogger().println("    " + item.toString());
            }
        }
    }

    private static Set<String> getTemplates(Collection<GeneratedJob> jobs) {
        Collection<String> templateNames = Collections2.transform(jobs, new Function<GeneratedJob, String>() {
            @Override
            public String apply(GeneratedJob input) {
                return input.getTemplateName();
            }
        });
        return new LinkedHashSet<String>(Collections2.filter(templateNames, Predicates.notNull()));
    }

    private static class SeedNamePredicate implements Predicate<SeedReference> {
        private final String seedJobName;

        SeedNamePredicate(String seedJobName) {
            this.seedJobName = seedJobName;
        }

        @Override
        public boolean apply(SeedReference input) {
            return seedJobName.equals(input.getSeedJobName());
        }
    }
}
