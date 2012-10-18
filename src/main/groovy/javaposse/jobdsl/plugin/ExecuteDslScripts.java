package javaposse.jobdsl.plugin;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.Builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedJob;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.base.Joiner;

/**
 * This Builder keeps a list of job DSL scripts, and when prompted, executes these to create /
 * update Jenkins jobs.
 *
 * @author jryan
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

    @DataBoundConstructor
    public ExecuteDslScripts(ScriptLocation scriptLocation) {
        // Copy over from embedded object
        this.usingScriptText = scriptLocation == null || scriptLocation.usingScriptText;
        this.targets = scriptLocation==null?null:scriptLocation.targets; // May be null;
        this.scriptText = scriptLocation==null?null:scriptLocation.scriptText; // May be null
    }

    ExecuteDslScripts(String scriptText) {
        this.usingScriptText = true;
        this.scriptText = scriptText;
        this.targets = null;
    }

    ExecuteDslScripts() { /// Where is the empty constructor called?
        super();
        this.usingScriptText = true;
        this.scriptText = null;
        this.targets = null;
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

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new GeneratedJobsAction(project);
    }

    /**
     * Runs every job DSL script provided in the plugin configuration, which results in new /
     * updated Jenkins jobs. The created / updated jobs are reported in the build result.
     *
     * @param build
     * @param launcher
     * @param listener
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
                    throws InterruptedException, IOException {
        EnvVars env = build.getEnvironment(listener);
        env.overrideAll(build.getBuildVariables());
        // TODO Use env to inject into DSL

        // We run the DSL, it'll need some way of grabbing a template config.xml and how to save it
        JenkinsJobManagement jm = new JenkinsJobManagement(listener.getLogger(), env);

        List<String> bodies = collectBodies(build, listener, env);
        Set<GeneratedJob> freshJobs = executeBodies(bodies, jm);

        // TODO Pull all this out, so that it can run outside of the plugin, e.g. JenkinsRestApiJobManagement
        updateTemplates(build, listener, freshJobs);
        updateGeneratedJobs(build, listener, freshJobs);

        // Save onto Builder, which belongs to a Project.
        GeneratedJobsBuildAction gjba = new GeneratedJobsBuildAction(freshJobs);
        gjba.getModifiedJobs().addAll(freshJobs); // Relying on Set to keep only unique values
        build.addAction(gjba);

        // Hint that our new jobs might have really shaken things up
        Jenkins.getInstance().rebuildDependencyGraph();

        return true;
    }

    private List<String> collectBodies(AbstractBuild<?, ?> build, BuildListener listener, EnvVars env) throws IOException, InterruptedException {
        List<String> bodies = Lists.newArrayList();
        if (usingScriptText) {
            listener.getLogger().println("Using dsl from string");
            bodies.add(scriptText);
        } else {
            String targetsStr = env.expand(this.targets);
            LOGGER.log(Level.FINE, String.format("Expanded targets to %s", targetsStr));
            String[] targets = targetsStr.split("\n");

            for (String target : targets) {
                FilePath targetPath = build.getModuleRoot().child(target);
                if (!targetPath.exists()) {
                    targetPath = build.getWorkspace().child(target);
                    if (!targetPath.exists()) {
                        throw new FileNotFoundException("Unable to find DSL script at " + target);
                    }
                }
                listener.getLogger().println(String.format("Running dsl from %s", targetPath));

                String dslBody = targetPath.readToString();
                bodies.add(dslBody);
            }
        }
        return bodies;
    }

    private Set<GeneratedJob> executeBodies(List<String> bodies, JenkinsJobManagement jm) {
        Set<GeneratedJob> freshJobs = Sets.newLinkedHashSet();
        for (String dslBody: bodies) {
            LOGGER.log(Level.FINE, String.format("DSL Content: %s", dslBody));

            // Room for one dsl to succeed and another to fail, yet jobs from the first will finish
            // TODO postpone saving jobs even later
            Set<GeneratedJob> dslJobs = DslScriptLoader.runDsl(dslBody, jm);

            freshJobs.addAll(dslJobs);
        }
        return freshJobs;
    }

    /**
     * Uses generatedJobs as existing data, so call before updating generatedJobs.
     * @param build
     * @param listener
     * @param freshJobs
     * @return
     * @throws IOException
     */
    private Set<String> updateTemplates(AbstractBuild<?, ?> build, BuildListener listener, Set<GeneratedJob> freshJobs) throws IOException {
        Set<String> freshTemplates = JenkinsJobManagement.getTemplates(freshJobs);
        Set<String> existingTemplates = JenkinsJobManagement.getTemplates(extractGeneratedJobs(build.getProject()));
        Set<String> newTemplates = Sets.difference(freshTemplates, existingTemplates);
        Set<String> removedTemplates = Sets.difference(existingTemplates, freshTemplates);

        listener.getLogger().println("Existing Templates: " + Joiner.on(",").join( existingTemplates ));
        listener.getLogger().println("New Templates: " + Joiner.on(",").join( newTemplates ));
        listener.getLogger().println("Unreferenced Templates: " + Joiner.on(",").join(removedTemplates));

        // Collect information about the templates we loaded
        final String seedJobName = build.getProject().getName();
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        boolean descriptorMutated = false;

        // Clean up
        for(String templateName: removedTemplates) {
            Collection<SeedReference> seedJobReferences = descriptor.getTemplateJobMap().get(templateName);
            Collection<SeedReference> matching = Collections2.filter(seedJobReferences, new SeedNamePredicate(seedJobName));
            if (!matching.isEmpty()) {
                seedJobReferences.removeAll(matching);
                descriptorMutated = true;
            }
        }

        // Ensure we have a reference
        for(String templateName: freshTemplates) {
            Collection<SeedReference> seedJobReferences = descriptor.getTemplateJobMap().get(templateName);
            Collection<SeedReference> matching = Collections2.filter(seedJobReferences, new SeedNamePredicate(seedJobName));

            AbstractProject templateProject = (AbstractProject) Jenkins.getInstance().getItem(templateName);
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


    /**
     * @param listener
     * @param freshJobs
     * @throws IOException
     */
    private void updateGeneratedJobs(final AbstractBuild<?, ?> build, BuildListener listener, Set<GeneratedJob> freshJobs) throws IOException {
        // Update Project
        Set<GeneratedJob> generatedJobs = extractGeneratedJobs(build.getProject());
        Set<GeneratedJob> added = Sets.difference(freshJobs, generatedJobs);
        Set<GeneratedJob> existing = Sets.intersection(generatedJobs, freshJobs);
        Set<GeneratedJob> removed = Sets.difference(generatedJobs, freshJobs);

        listener.getLogger().println("Adding jobs: "   + Joiner.on(",").join(added));
        listener.getLogger().println("Existing jobs: " + Joiner.on(",").join(existing));
        listener.getLogger().println("Removing jobs: " + Joiner.on(",").join(removed));

        // Update unreferenced jobs
        for(GeneratedJob removedJob: removed) {
            AbstractProject removedProject = (AbstractProject) Jenkins.getInstance().getItem(removedJob.getJobName());
            // TODO Let user choose what to do
            removedProject.disable(); // TODO deleteJob which is protected
        }

        // BuildAction is created with the result, we'll look at an aggregation of builds to know figure out our generated jobs

    }

    private Set<GeneratedJob> extractGeneratedJobs(AbstractProject project) {
        GeneratedJobsAction gja = project.getAction(GeneratedJobsAction.class);
        if (gja==null || gja.findLastGeneratedJobs() == null) {
            return Sets.newHashSet();
        } else {
            return gja.findLastGeneratedJobs();
        }
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<Builder> {

        private Multimap<String, SeedReference> templateJobMap; // K=templateName, V=Seed

        public DescriptorImpl() {
            super(ExecuteDslScripts.class);
            load();
        }

        public String getDisplayName() {
            return "Process Job DSLs";
        }

        public Multimap<String, SeedReference> getTemplateJobMap() {
            if (templateJobMap == null) {
                templateJobMap = HashMultimap.create();
            }

            return templateJobMap;
        }

        public void setTemplateJobMap(Multimap<String, SeedReference> templateJobMap) {
            this.templateJobMap = templateJobMap;
        }

        static final String defaultDsl = "job {\n" +
                "    using 'TMPL-test'\n" +
                "    name 'PROJ-integ-tests'\n" +
                "    configure { node ->\n" +
                "        configureScm(node)\n" +
                "        triggers.'hudson.triggers.TimerTrigger'.spec = '15 1,13 * * *'\n" +
                "        goals = '-e clean integTest'\n" +
                "    }\n" +
                "}\n";

        /*
        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData);
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            return super.configure(req, json);
        }
        */
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
