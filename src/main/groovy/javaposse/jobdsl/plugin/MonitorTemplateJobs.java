package javaposse.jobdsl.plugin;

import java.util.logging.Logger;

import jenkins.model.Jenkins;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Cause;
import hudson.model.Saveable;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.listeners.SaveableListener;

@Extension
public class MonitorTemplateJobs extends SaveableListener {
    private static final Logger LOGGER = Logger.getLogger(MonitorTemplateJobs.class.getName());

    public MonitorTemplateJobs() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onChange(Saveable saveable, XmlFile file) {
        LOGGER.fine("onChange");
        if( !AbstractProject.class.isAssignableFrom(saveable.getClass()) ) {
            LOGGER.finer("Is not a Project");
            return;
        }

        // Look for template jobs
        AbstractProject project = (AbstractProject) saveable;
        SeedJobsProperty seedJobsProp = (SeedJobsProperty) project.getProperty(SeedJobsProperty.class);
        if (seedJobsProp == null || seedJobsProp.seedJobs == null) {
            LOGGER.finer("Is not a Template Project");
            return;
        }

        // If Template is changing, we need to kick off all see jobs
        for(String seedJob: seedJobsProp.getSeedJobs()) {
            AbstractProject seedProject = (AbstractProject) Jenkins.getInstance().getItem(seedJob);
            seedProject.scheduleBuild(30, new TemplateTriggerCause());
        }
    }

    public static class TemplateTriggerCause extends Cause {
        public TemplateTriggerCause() {
        }

        @Override
        public void onAddedTo(AbstractBuild build) {
            LOGGER.info("TemplateTriggerCause.onAddedTo");
        }

        @Override
        public String getShortDescription() {
            return "Template has changed";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof TemplateTriggerCause;
        }

        @Override
        public int hashCode() {
            return 3;
        }
    }

}
