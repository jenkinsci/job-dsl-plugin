package javaposse.jobdsl;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.XmlFile;
import hudson.model.BuildListener;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.model.TopLevelItem;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.collect.Sets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamSource;

import jenkins.model.Jenkins;

public class ExecuteDslScripts extends Builder {
    private static final Logger LOGGER = Logger.getLogger(ExecuteDslScripts.class.getName());

   /**
    * newlines separated list of locations to dsl scripts
    */
   private final String targets;

   @DataBoundConstructor
   public ExecuteDslScripts(String targets) {
       this.targets = Util.fixEmptyAndTrim(targets);
   }

   public String getTargets() {
       return targets;
   }

   @Override
   public boolean perform(final AbstractBuild<?,?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
       EnvVars env = build.getEnvironment(listener);
       env.overrideAll(build.getBuildVariables());

       String targetsStr = env.expand(this.targets);
       LOGGER.log(Level.FINE, String.format("Expanded targets to %s", targetsStr));
       String[] targets = targetsStr.split("\n");

       // Track what jobs got created/updated
       Set<String> modifiedJobs = Sets.newHashSet();

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
           JobManagement jm = new JobManagement() {
                Jenkins jenkins = Jenkins.getInstance();

                @Override
                public String getConfig(String jobName) throws IOException {
                    LOGGER.log(Level.INFO, String.format("Getting config for %s", jobName));
                    AbstractProject<?,?> project = (AbstractProject<?,?>) jenkins.getItemByFullName(jobName);
                    XmlFile xmlFile = project.getConfigFile();
                    String xml = xmlFile.asString();
                    LOGGER.log(Level.FINE, String.format("Job config %s", xml));
                    return xml;
                }

                @Override
                public void createOrUpdateConfig(String jobName, String config) throws IOException {
                    LOGGER.log(Level.INFO, String.format("createOrUpdateConfig for %s", jobName));
                    AbstractProject<?,?> project = (AbstractProject<?,?>) jenkins.getItemByFullName(jobName);
                    if (project == null) {
                        // Creating project
                        LOGGER.log(Level.FINE, String.format("Creating project as %s", config));
                        InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));  // TODO confirm that we're using UTF-8
                        TopLevelItem item = jenkins.createProjectFromXML(jobName, is);
                    } else {
                        LOGGER.log(Level.FINE, String.format("Updating project as %s", config));
                        StreamSource streamSource = new StreamSource(new StringReader(config)); // TODO use real xmlReader
                        project.updateByXml(streamSource);
                    }
                }
           };
           
           DslScriptLoader.runDsl(dslBody, jm);
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
