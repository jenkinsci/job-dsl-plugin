package org.jenkinsci.plugins.associatedfiles

import hudson.model.Action


public class AssociatedFilesAction implements Action {
  String buildAssociatedFiles
  
  public AssociatedFilesAction(String buildAssociatedFiles) {
    this.buildAssociatedFiles = buildAssociatedFiles
  }
  
  public String getIconFileName() {
    return null
  }
  
  public String getDisplayName() {
    return null
  }
  
  public String getUrlName() {
    return "associatedFiles"
  }
  
  public getBuildAssociatedFilesList() {
    return buildAssociatedFiles.split(',').collect { it.trim() }
  }
}
