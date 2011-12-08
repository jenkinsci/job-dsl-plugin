package org.jenkinsci.plugins.associatedfiles

import hudson.Extension
import hudson.model.AbstractProject
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Publisher


@Extension
public class AssociatedFilesPublisherDescriptor extends BuildStepDescriptor<Publisher> {

  public AssociatedFilesPublisherDescriptor() {
    super(AssociatedFilesPublisher.class)
    load()
  }
  
  @Override
  public boolean isApplicable(Class<? extends AbstractProject> jobType) {
    return true
  }
  
  @Override
  public String getDisplayName() {
    return "Associate non-archived files"
  }
}
