package javaposse.jobdsl.plugin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import hudson.Util;
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
        LOGGER.info("onChange");

        if( !AbstractProject.class.isAssignableFrom(saveable.getClass()) ) {
            LOGGER.fine(String.format("%s is not a Project", saveable.getClass()));
            return;
        }

        // Look for template jobs
        AbstractProject project = (AbstractProject) saveable;
        SeedJobsProperty seedJobsProp = (SeedJobsProperty) project.getProperty(SeedJobsProperty.class);
        if (seedJobsProp == null || seedJobsProp.seedJobs == null) {
            LOGGER.fine(String.format("%s is not a Template Project", project.getName()));
            return;
        }

        // If Template is changing, we need to kick off all see jobs
        LOGGER.fine(String.format("%s is a Template", project.getName()));
        String digest;
        try {
            digest = Util.getDigestOf(new FileInputStream(file.getFile()));
        } catch (IOException e) {
            LOGGER.warning(String.format("Unable to calculate digest from file for %s", project.getName()));
            return;
        }

        for(Map.Entry<String,String> entry: seedJobsProp.seedJobs.entrySet()) {
            String seedJob = entry.getKey();
            String previousDigest = entry.getValue();
            if (digest.equals(previousDigest)) {
                LOGGER.fine(String.format("Previously seen %s seed job", seedJob));
                continue;
            }
            AbstractProject seedProject = (AbstractProject) Jenkins.getInstance().getItem(seedJob);
            if (seedProject == null) {
                LOGGER.fine(String.format("Downstream project %s not found", seedJob)); // TODO use this excuse to do cleanup
                continue;
            }

            LOGGER.fine(String.format("Scheduling %s, since it's downstream", seedJob));
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
