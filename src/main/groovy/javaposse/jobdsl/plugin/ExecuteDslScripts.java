package javaposse.jobdsl.plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedJob;
import jenkins.model.Jenkins;

import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.kohsuke.stapler.StaplerRequest;

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

    /**
     * Track what jobs got created/updated, we don't want to depend on the builds
     */
    Set<GeneratedJob> generatedJobs;

    /**
     * Track what Templates were used to generate the jobs
     */
    private Map<String,String> templateJobs;

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

    public Set<GeneratedJob> getGeneratedJobs() {
        return generatedJobs;
    }

    void setGeneratedJobs(Set<GeneratedJob> generatedJobs) {
        this.generatedJobs = generatedJobs;
    }

    public Map<String, String> getTemplateJobs() {
        return templateJobs;
    }

    public void setTemplateJobs(Map<String, String> templateJobs) {
        this.templateJobs = templateJobs;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        if (generatedJobs == null) {
            return new GeneratedJobsAction();
        } else {
            return new GeneratedJobsAction(generatedJobs);
        }
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

        List<String> bodies = collectBodies(build, listener, env);

        // We run the DSL, it'll need some way of grabbing a template config.xml and how to save it
        JenkinsJobManagement jm = new JenkinsJobManagement(listener.getLogger());

        Set<GeneratedJob> freshJobs = Sets.newHashSet();
        for (String dslBody: bodies) {
            LOGGER.log(Level.FINE, String.format("DSL Content: %s", dslBody));

            // Room for one dsl to succeed and another to fail, yet jobs from the first will finish
            // TODO postpone saving jobs even later
            Set<GeneratedJob> generatedJobs = DslScriptLoader.runDsl(dslBody, jm);

            freshJobs.addAll(generatedJobs);
        }

        if (generatedJobs == null) {
            generatedJobs = Sets.newHashSet();
        }
        // TODO Pull all this out, so that it can run outside of the plugin, e.g. JenkinsRestApiJobManagement

        // Update Project
        Set<GeneratedJob> removedJobs = Sets.difference(generatedJobs, freshJobs);
        // TODO Print to listener, so that it shows up in the build
        listener.getLogger().println("Adding jobs: "   + Joiner.on(",").join( Sets.difference(freshJobs, generatedJobs) ));
        listener.getLogger().println("Existing jobs: " + Joiner.on(",").join( Sets.intersection(generatedJobs, freshJobs) ));
        listener.getLogger().println("Removing jobs: " + Joiner.on(",").join(removedJobs));

        // Update unreferenced jobs
        for(GeneratedJob removedJob: removedJobs) {
            AbstractProject removedProject = (AbstractProject) Jenkins.getInstance().getItem(removedJob.getJobName());
            removedProject.disable(); // TODO deleteJob which is protected
        }

        // Update Templates
        if (templateJobs == null) {
            templateJobs = Maps.newHashMap();
        }

        Set<String> freshTemplates = JenkinsJobManagement.getTemplates(freshJobs);
        Set<String> existingTemplates = templateJobs.keySet();
        Set<String> newTemplates = Sets.difference(freshTemplates, existingTemplates);
        Set<String> removedTemplates = Sets.difference(existingTemplates, freshTemplates);

        listener.getLogger().println("Existing Templates: " + Joiner.on(",").join( existingTemplates ));
        listener.getLogger().println("New Templates: " + Joiner.on(",").join( newTemplates ));
        listener.getLogger().println("Unreferenced Templates: " + Joiner.on(",").join(removedTemplates));

        // Collect information about the templates we loaded
        String seedJobName = build.getProject().getName();
        Map<String, String> freshTemplateMap = Maps.newHashMap();
        for(String templateProjectName: freshTemplates) {
            AbstractProject templateProject = (AbstractProject) Jenkins.getInstance().getItem(templateProjectName);
            String digest = Util.getDigestOf(new FileInputStream(templateProject.getConfigFile().getFile()));
            freshTemplateMap.put(templateProjectName, digest);
        }

        // Add GeneratedJobsBuildAction to Build
        GeneratedJobsBuildAction gjba = new GeneratedJobsBuildAction(freshJobs);
        gjba.getModifiedJobs().addAll(freshJobs); // Relying on Set to keep only unique values
        build.addAction(gjba);

        // Save onto Builder, which belongs to a Project.
        generatedJobs = Sets.newHashSet(freshJobs);
        templateJobs = Maps.newHashMap(freshTemplateMap);
        build.getProject().save();

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


    @Extension
    public static final class DescriptorImpl extends Descriptor<Builder> {
        public String getDisplayName() {
            return "Process Job DSLs";
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

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData);
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            return super.configure(req, json);
        }
    }

}
