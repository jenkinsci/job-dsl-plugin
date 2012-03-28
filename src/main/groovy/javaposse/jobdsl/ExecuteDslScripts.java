package javaposse.jobdsl;

import hudson.Launcher;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.tasks.Fingerprinter;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.XmlFile;
import java.io.Reader;
import java.io.IOException;
import hudson.CopyOnWrite;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Functions;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.EnvironmentSpecific;
import jenkins.model.Jenkins;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.DownloadFromUrlInstaller;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import hudson.util.ArgumentListBuilder;
import hudson.util.VariableResolver;
import hudson.util.FormValidation;
import hudson.util.XStream2;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import javax.xml.transform.stream.StreamSource;

class ExecuteDslScripts extends Builder {
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
   public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
       EnvVars env = build.getEnvironment(listener);
       env.overrideAll(build.getBuildVariables());

       String targetsStr = env.expand(this.targets);
       String[] targets = targetsStr.split("\n");

       for(String target: targets) {
           FilePath targetPath = build.getModuleRoot().child(target);
           if (!targetPath.exists()) {
               targetPath = build.getWorkspace().child(target);
               if(!targetPath.exists()) {
                   listener.fatalError("Unable to find DSL script at "+ target);
                   return false;
               }
           }
           // We run the DSL, it'll need someway of grabbing a template config.xml and how to save it
           // They'll make REST calls
           AbstractProject<?,?> project = build.getProject();
           XmlFile xmlFile = project.getConfigFile();
           Reader xmlReader = xmlFile.readRaw();

           StreamSource streamSource = new StreamSource(xmlReader); // TODO use real xmlReader
           project.updateByXml(streamSource);
       }
       return true;
   }

   @Extension
   public static final class DescriptorImpl extends Descriptor<Builder> {
       public String getDisplayName() {
           return "Job Dsl Plugin";
           //return Messages.CreateFingerprint_DisplayName();
       }
   }

}
