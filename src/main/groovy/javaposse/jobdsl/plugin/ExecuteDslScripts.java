package javaposse.jobdsl.plugin;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.DslScriptLoader;

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
   public boolean perform(final AbstractBuild<?,?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
       EnvVars env = build.getEnvironment(listener);
       env.overrideAll(build.getBuildVariables());

       String targetsStr = env.expand(this.targets);
       LOGGER.log(Level.FINE, String.format("Expanded targets to %s", targetsStr));
       String[] targets = targetsStr.split("\n");

       // Track what jobs got created/updated
       Set<String> modifiedJobs = Sets.newHashSet();
       Set<String> referencedTemplates = Sets.newHashSet();
       
       for(String target: targets) {
           FilePath targetPath = build.getModuleRoot().child(target);
           if (!targetPath.exists()) {
               targetPath = build.getWorkspace().child(target);
               if(!targetPath.exists()) {
                   listener.fatalError("Unable to find DSL script at "+ target);
                   return false;
               }
           }
           LOGGER.log(Level.INFO, String.format("Running dsl from %s", targetPath));

           String dslBody = targetPath.readToString();
           LOGGER.log(Level.FINE, String.format("DSL Content: %s", dslBody));

           // We run the DSL, it'll need some way of grabbing a template config.xml and how to save it
           // They'll make REST calls, we'll make internal Jenkins calls
           JenkinsJobManagement jm = new JenkinsJobManagement();
           
           DslScriptLoader.runDsl(dslBody, jm);
           
           referencedTemplates.addAll(jm.referencedTemplates);
       }

       // Add GeneratedJobsAction
       GeneratedJobsBuildAction gjba = new GeneratedJobsBuildAction(modifiedJobs);
       build.addAction(gjba);

       return true;
   }

   @Extension
   public static final class DescriptorImpl extends Descriptor<Builder> {
       public String getDisplayName() {
           return "Process Job DSLs";
       }
   }

}
