package javaposse.jobdsl.plugin;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Item;
import hudson.model.Result;
import hudson.tasks.Builder;
import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.GeneratedView;
import javaposse.jobdsl.dsl.ScriptRequest;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

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

    private final LookupStrategy lookupStrategy;

    @DataBoundConstructor
    public ExecuteDslScripts(ScriptLocation scriptLocation, boolean ignoreExisting, RemovedJobAction removedJobAction,
                             LookupStrategy lookupStrategy) {
        // Copy over from embedded object
        this.usingScriptText = scriptLocation == null || scriptLocation.usingScriptText;
        this.targets = scriptLocation == null ? null : scriptLocation.targets;
        this.scriptText = scriptLocation == null ? null : scriptLocation.scriptText;
        this.ignoreExisting = ignoreExisting;
        this.removedJobAction = removedJobAction;
        this.lookupStrategy = lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public ExecuteDslScripts(ScriptLocation scriptLocation, boolean ignoreExisting, RemovedJobAction removedJobAction) {
        this(scriptLocation, ignoreExisting, removedJobAction, LookupStrategy.JENKINS_ROOT);
    }

    ExecuteDslScripts(String scriptText) {
        this.usingScriptText = true;
        this.scriptText = scriptText;
        this.targets = null;
        this.ignoreExisting = false;
        this.removedJobAction = RemovedJobAction.DISABLE;
        this.lookupStrategy = LookupStrategy.JENKINS_ROOT;
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

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        return asList(new GeneratedJobsAction(project), new GeneratedViewsAction(project));
    }

    /**
     * Runs every job DSL script provided in the plugin configuration, which results in new /
     * updated Jenkins jobs. The created / updated jobs are reported in the build result.
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher,
                           final BuildListener listener) throws InterruptedException, IOException {
        EnvVars env = build.getEnvironment(listener);
        env.putAll(build.getBuildVariables());

        // We run the DSL, it'll need some way of grabbing a template config.xml and how to save it
        JenkinsJobManagement jm = new JenkinsJobManagement(listener.getLogger(), env, build, lookupStrategy);

        ScriptRequestGenerator generator = new ScriptRequestGenerator(build, env);
        Set<ScriptRequest> scriptRequests = generator.getScriptRequests(targets, usingScriptText, scriptText, ignoreExisting);

        Set<GeneratedJob> freshJobs = Sets.newLinkedHashSet();
        Set<GeneratedView> freshViews = Sets.newLinkedHashSet();
        for (ScriptRequest request : scriptRequests) {
            LOGGER.log(Level.FINE, String.format("Request for %s", request.location));

            GeneratedItems generatedItems = DslScriptLoader.runDslEngine(request, jm);
            freshJobs.addAll(generatedItems.getJobs());
            freshViews.addAll(generatedItems.getViews());
        }

        Set<GeneratedJob> failedJobs = new HashSet<GeneratedJob>();
        for (GeneratedJob gj : freshJobs) {
            if (gj.isCreated()) {
                failedJobs.add(gj);
            }
        }

        if (!failedJobs.isEmpty()) {
            listener.getLogger().println("Failed jobs: " + Joiner.on(",").join(failedJobs));
            build.setResult(Result.UNSTABLE);
        }

        updateTemplates(build, listener, freshJobs);
        updateGeneratedJobs(build, listener, freshJobs);
        updateGeneratedViews(build, listener, freshViews);

        // Save onto Builder, which belongs to a Project.
        GeneratedJobsBuildAction gjba = new GeneratedJobsBuildAction(freshJobs, lookupStrategy);
        gjba.getModifiedJobs().addAll(freshJobs); // Relying on Set to keep only unique values
        build.addAction(gjba);
        GeneratedViewsBuildAction gvba = new GeneratedViewsBuildAction(freshViews, lookupStrategy);
        gvba.getModifiedViews().addAll(freshViews); // Relying on Set to keep only unique values
        build.addAction(gvba);

        // Hint that our new jobs might have really shaken things up
        Jenkins.getInstance().rebuildDependencyGraph();

        return true;
    }


    /**
     * Uses generatedJobs as existing data, so call before updating generatedJobs.
     */
    private Set<String> updateTemplates(AbstractBuild<?, ?> build, BuildListener listener,
                                        Set<GeneratedJob> freshJobs) throws IOException {
        AbstractProject<?, ?> seedJob = build.getProject();

        Set<String> freshTemplates = JenkinsJobManagement.getTemplates(freshJobs);
        Set<String> existingTemplates = JenkinsJobManagement.getTemplates(extractGeneratedJobs(seedJob));
        Set<String> newTemplates = Sets.difference(freshTemplates, existingTemplates);
        Set<String> removedTemplates = Sets.difference(existingTemplates, freshTemplates);

        listener.getLogger().println("Existing Templates: " + Joiner.on(",").join(existingTemplates));
        listener.getLogger().println("New Templates: " + Joiner.on(",").join(newTemplates));
        listener.getLogger().println("Unreferenced Templates: " + Joiner.on(",").join(removedTemplates));

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

            AbstractProject templateProject = lookupStrategy.getItem(seedJob, templateName, AbstractProject.class);
            final String digest = Util.getDigestOf(new FileInputStream(templateProject.getConfigFile().getFile()));

            if (matching.size() == 1) {
                // Just update digest
                SeedReference ref = Iterables.get(matching, 0);
                if (digest.equals(ref.digest)) {
                    ref.digest = digest;
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
                                     Set<GeneratedJob> freshJobs) throws IOException {
        // Update Project
        Set<GeneratedJob> generatedJobs = extractGeneratedJobs(build.getProject());
        Set<GeneratedJob> added = Sets.difference(freshJobs, generatedJobs);
        Set<GeneratedJob> existing = Sets.intersection(generatedJobs, freshJobs);
        Set<GeneratedJob> removed = Sets.difference(generatedJobs, freshJobs);

        listener.getLogger().println("Adding items: " + Joiner.on(",").join(added));
        listener.getLogger().println("Existing items: " + Joiner.on(",").join(existing));
        listener.getLogger().println("Removing items: " + Joiner.on(",").join(removed));

        // Update unreferenced jobs
        for (GeneratedJob removedJob : removed) {
            Item removedItem = lookupStrategy.getItem(build.getProject(), removedJob.getJobName(), Item.class);
            if (removedItem != null && removedJobAction != RemovedJobAction.IGNORE) {
                if (removedJobAction == RemovedJobAction.DELETE) {
                    try {
                        removedItem.delete();
                    } catch (InterruptedException e) {
                        listener.getLogger().println(String.format("Delete item failed: %s", removedJob));
                        if (removedItem instanceof AbstractProject) {
                            listener.getLogger().println(String.format("Disabling item instead: %s", removedJob));
                            ((AbstractProject) removedItem).disable();
                        }
                    }
                } else {
                    if (removedItem instanceof AbstractProject) {
                        ((AbstractProject) removedItem).disable();
                    }
                }
            }
        }
    }

    private Set<GeneratedJob> extractGeneratedJobs(AbstractProject project) {
        GeneratedJobsAction gja = project.getAction(GeneratedJobsAction.class);
        if (gja == null) {
            return Sets.newLinkedHashSet();
        } else {
            return gja.findLastGeneratedJobs();
        }
    }

    private void updateGeneratedViews(AbstractBuild<?, ?> build, BuildListener listener, Set<GeneratedView> freshViews) {
        Set<GeneratedView> generatedViews = extractGeneratedViews(build.getProject());
        Set<GeneratedView> added = Sets.difference(freshViews, generatedViews);
        Set<GeneratedView> existing = Sets.intersection(generatedViews, freshViews);
        Set<GeneratedView> removed = Sets.difference(generatedViews, freshViews);

        listener.getLogger().println("Adding views: " + Joiner.on(",").join(added));
        listener.getLogger().println("Existing views: " + Joiner.on(",").join(existing));
        listener.getLogger().println("Removing views: " + Joiner.on(",").join(removed));
    }

    private Set<GeneratedView> extractGeneratedViews(AbstractProject<?, ?> project) {
        GeneratedViewsAction gja = project.getAction(GeneratedViewsAction.class);
        if (gja == null) {
            return Sets.newLinkedHashSet();
        } else {
            return gja.findLastGeneratedViews();
        }
    }

    private static class SeedNamePredicate implements Predicate<SeedReference> {
        private final String seedJobName;

        public SeedNamePredicate(String seedJobName) {
            this.seedJobName = seedJobName;
        }

        @Override
        public boolean apply(SeedReference input) {
            return seedJobName.equals(input.seedJobName);
        }
    }
}
