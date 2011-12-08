package org.jenkinsci.plugins.associatedfiles

import hudson.Extension
import hudson.FilePath
import hudson.Launcher
import hudson.Util
import hudson.model.*
import hudson.model.listeners.RunListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.BuildStepMonitor
import hudson.tasks.Publisher
import hudson.tasks.Recorder
import hudson.util.DescribableList
import hudson.util.FormValidation
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import java.util.logging.Level;
import java.util.logging.Logger;


import org.kohsuke.stapler.AncestorInPath
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.DataBoundConstructor

/**
 * @author Andrew Bayer
 */
public class AssociatedFilesPublisher extends Recorder {
  String associatedFiles

  @DataBoundConstructor
  public AssociatedFilesPublisher(String associatedFiles) {
    this.associatedFiles = associatedFiles
  }

  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.NONE;
  }

  public getAssociatedFilesList() {
    return associatedFiles.split(',').collect { it.trim() }
  }


  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    AssociatedFilesAction afa = new AssociatedFilesAction(build.getEnvironment(listener).expand(associatedFiles))
    build.addAction(afa)
    return true;
  }

}  


