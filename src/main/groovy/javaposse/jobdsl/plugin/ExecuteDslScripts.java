package javaposse.jobdsl.plugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.DslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedJob;
import jenkins.model.Jenkins;

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
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
                    throws InterruptedException, IOException {
        EnvVars env = build.getEnvironment(listener);
        env.overrideAll(build.getBuildVariables());

        String targetsStr = env.expand(this.targets);
        LOGGER.log(Level.FINE, String.format("Expanded targets to %s", targetsStr));
        String[] targets = targetsStr.split("\n");

        // Track what jobs got created/updated
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

            // We run the DSL, it'll need some way of grabbing a template config.xml and how to save it
            // They'll make REST calls, we'll make internal Jenkins calls
            JenkinsJobManagement jm = new JenkinsJobManagement();

            // Room for one dsl to succeed and another to fail, yet jobs from the first will finish
            // TODO postpone saving jobs even later
            Set<GeneratedJob> generatedJobs = DslScriptLoader.runDsl(dslBody, jm);

            modifiedJobs.addAll(generatedJobs);
        }

        GeneratedJobsAction gja = build.getProject().getAction(GeneratedJobsAction.class);
        if (gja == null) {
            gja = new GeneratedJobsAction();
            build.getProject().addAction(gja);
        }

        // Capture some data first
        Set<String> existingTemplates = Sets.newHashSet(getTemplates(gja.modifiedJobs));
        Set<GeneratedJob> existingGeneratedJobs = Sets.newHashSet(gja.modifiedJobs);

        // Add GeneratedJobsBuildAction to Build
        GeneratedJobsBuildAction gjba = new GeneratedJobsBuildAction(modifiedJobs);
        build.addAction(gjba);
        gja.getGeneratedJobs().addAll(modifiedJobs); // Relying on Set to keep only unique values

        // Update Project
        Set<GeneratedJob> removedJobs = Sets.difference(existingGeneratedJobs, modifiedJobs);
        LOGGER.info("Adding jobs: " + Joiner.on(",").join( Sets.difference(modifiedJobs, existingGeneratedJobs) ));
        LOGGER.info("Existing jobs: " + Joiner.on(",").join( Sets.intersection(existingGeneratedJobs, modifiedJobs) ));
        LOGGER.info("Removing jobs: " + Joiner.on(",").join(removedJobs));
        gja.getGeneratedJobs().clear();
        gja.getGeneratedJobs().addAll(modifiedJobs);

        // Update unreferenced jobs
        Jenkins.getInstance().getProjects().removeAll( getJobsByGeneratedJobs(removedJobs) ); // TODO not sure if this works, or if this is what people want

        // Update Templates
        Set<String> templates = getTemplates(gja.modifiedJobs);
        Set<String> newTemplates = Sets.difference(templates, existingTemplates);
        Set<String> removedTemplates = Sets.difference(existingTemplates, newTemplates);
        Set<String> modifyTemplates = Sets.newHashSet(Iterables.concat(newTemplates,removedTemplates));

        // Processing new and old together to simplify all the job lookup code
        String seedJobName = build.getProject().getName();
        for(Project<?,?> templateProject: getJobsByName(modifyTemplates)) {
            SeedJobsAction seedJobsAction = (SeedJobsAction) templateProject.getAction(SeedJobsAction.class);
            if (seedJobsAction == null) {
                seedJobsAction = new SeedJobsAction();
                templateProject.addAction(seedJobsAction);
            }
            String name = templateProject.getName();
            if (removedTemplates.contains(name)) { // Clean up templates which are no longer referenced
                seedJobsAction.seedJobs.remove(seedJobName);
            } else { // Add breadcrumbs to referenced templates
                seedJobsAction.seedJobs.add(seedJobName);
            }
        }

        return true;
    }

    public Collection<Project> getJobsByName(final Set<String> names) {
        return Collections2.filter(Jenkins.getInstance().getProjects(), new Predicate<Project>() {
            @Override public boolean apply(Project project) {
                return names.contains(project.getName());
            }
        });
    }

    public Collection<Project> getJobsByGeneratedJobs(final Set<GeneratedJob> generatedJobs) {
        Set<String> jobNames = Sets.newHashSet(Collections2.transform(generatedJobs, new ExtractTemplate()));
        return getJobsByName(jobNames);
    }

    public Set<String> getTemplates(Collection<GeneratedJob> jobs) {
        return Sets.newHashSet(Collections2.transform(jobs, new ExtractTemplate()));
    }

    public static class ExtractJobName implements Function<GeneratedJob, String> {
        @Override public String apply(GeneratedJob input) {
            return input.getJobName();
        }
    }

    public static class ExtractTemplate implements Function<GeneratedJob, String> {
        @Override public String apply(GeneratedJob input) {
            return input.getTemplateName();
        }
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<Builder> {
        public String getDisplayName() {
            return "Process Job DSLs";
        }
    }

}
