package org.jenkinsci.plugins.associatedfiles

import hudson.Extension
import hudson.model.AbstractBuild
import hudson.model.listeners.RunListener
import java.util.logging.Level;
import java.util.logging.Logger;

  
@Extension
public class AssociatedFilesRunListener extends RunListener<AbstractBuild> {
  private final Logger log = Logger.getLogger(AssociatedFilesRunListener.class.getName());
  
  public void onDeleted(AbstractBuild build) {
    AssociatedFilesAction afa = build.getAction(AssociatedFilesAction.class)
    
    if (afa == null)
      return
    
    log.warning("Processing files/dirs to delete - raw version is ${afa.buildAssociatedFiles}")
    afa.getBuildAssociatedFilesList().each { afRaw ->
      def afName = 
      log.warning("Checking associated file ${afName}")
      def afFile = new File(afName)
      
      if (afFile.isDir()) {
          log.warning("Deleting directory ${afName}")
          if (!afFile.deleteDir()) {
            log.warning("Could not delete directory ${afName}")
          }
      }
      else if (afFile.isFile()) {
        log.warning("Deleting file ${afName}")
        if (!afFile.delete()) {
          log.warning("Could not delete file ${afName}")
        }
      }
    }
  } 
}
