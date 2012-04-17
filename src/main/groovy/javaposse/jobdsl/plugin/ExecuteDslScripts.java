package javaposse.jobdsl.plugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedJob;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * This Builder keeps a list of job DSL scripts, and when prompted, executes these to create /
 * update Jenkins jobs.
 * 
 * @author jryan
 */
public class ExecuteDslScripts extends Builder {
    private static final Logger LOGGER = Logger.getLogger(ExecuteDslScripts.class.getName());

    /**
     * Newline-separated list of locations to dsl scripts
     */
    private final String targets;

    @DataBoundConstructor
    public ExecuteDslScripts(String targets) {
        this.targets = Util.fixEmptyAndTrim(targets);
    }

    public String getTargets() {
        return targets;
    }

    // Track what jobs got created/updated, we don't want to depend on the builds
    Set<GeneratedJob> generatedJobs;

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

        String targetsStr = env.expand(this.targets);
        LOGGER.log(Level.FINE, String.format("Expanded targets to %s", targetsStr));
        String[] targets = targetsStr.split("\n");

        // We run the DSL, it'll need some way of grabbing a template config.xml and how to save it
        // They'll make REST calls, we'll make internal Jenkins calls
        JenkinsJobManagement jm = new JenkinsJobManagement();

        Set<GeneratedJob> modifiedJobs = Sets.newHashSet();
        for (String target : targets) {
            FilePath targetPath = build.getModuleRoot().child(target);
            if (!targetPath.exists()) {
                targetPath = build.getWorkspace().child(target);
                if (!targetPath.exists()) {
                    listener.fatalError("Unable to find DSL script at " + target);
                    return false;
                }
            }
            LOGGER.log(Level.INFO, String.format("Running dsl from %s", targetPath));

            String dslBody = targetPath.readToString();
            LOGGER.log(Level.FINE, String.format("DSL Content: %s", dslBody));

            // Room for one dsl to succeed and another to fail, yet jobs from the first will finish
            // TODO postpone saving jobs even later
            Set<GeneratedJob> generatedJobs = DslScriptLoader.runDsl(dslBody, jm);

            modifiedJobs.addAll(generatedJobs);
        }

        if (generatedJobs == null) {
            generatedJobs = Sets.newHashSet();
        }

        // Update Project
        Set<GeneratedJob> removedJobs = Sets.difference(generatedJobs, modifiedJobs);
        // TODO Print to listener, so that it shows up in the build
        LOGGER.info("Adding jobs: " + Joiner.on(",").join( Sets.difference(modifiedJobs, generatedJobs) )); // TODO only bring jobNames
        LOGGER.info("Existing jobs: " + Joiner.on(",").join( Sets.intersection(generatedJobs, modifiedJobs) ));
        LOGGER.info("Removing jobs: " + Joiner.on(",").join(removedJobs));

        // Update unreferenced jobs
        for(GeneratedJob removedJob: removedJobs) {
            AbstractProject removedProject = (AbstractProject) Jenkins.getInstance().getItem(removedJob.getJobName());
            removedProject.disable(); // TODO deleteJob which is protected
        }

        // Update Templates
        Set<String> templates = JenkinsJobManagement.getTemplates(modifiedJobs);
        Set<String> existingTemplates = JenkinsJobManagement.getTemplates(generatedJobs);
        Set<String> newTemplates = Sets.difference(templates, existingTemplates);
        Set<String> removedTemplates = Sets.difference(existingTemplates, templates);
        Set<String> modifyTemplates = Sets.newHashSet(Iterables.concat(newTemplates,removedTemplates));

        // Add GeneratedJobsBuildAction to Build
        GeneratedJobsBuildAction gjba = new GeneratedJobsBuildAction(modifiedJobs);
        build.addAction(gjba);
        gjba.getModifiedJobs().addAll(modifiedJobs); // Relying on Set to keep only unique values

        // Save onto Builder
        generatedJobs = Sets.newHashSet(modifiedJobs);

        // Processing new and old together to simplify all the job lookup code
        String seedJobName = build.getProject().getName();
        for(String templateProjectName: modifyTemplates) {
            AbstractProject templateProject = (AbstractProject) Jenkins.getInstance().getItem(templateProjectName);
            SeedJobsProperty seedJobsProp = (SeedJobsProperty) templateProject.getProperty(SeedJobsProperty.class);
            if (seedJobsProp == null) {
                seedJobsProp = new SeedJobsProperty();
                templateProject.addProperty(seedJobsProp);
            }
            String name = templateProject.getName();
            if (removedTemplates.contains(name)) { // Clean up templates which are no longer referenced
                seedJobsProp.seedJobs.remove(seedJobName);
            } else { // Add breadcrumbs to referenced templates
                seedJobsProp.seedJobs.add(seedJobName);
            }
        }

        return true;
    }


    @Extension
    public static final class DescriptorImpl extends Descriptor<Builder> {
        public String getDisplayName() {
            return "Process Job DSLs";
        }
    }

}
