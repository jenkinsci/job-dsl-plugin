package javaposse.jobdsl.plugin;

import java.util.Set;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

public class SeedJobItemListener extends ItemListener {
    private static final Logger LOGGER = Logger.getLogger(SeedJobItemListener.class.getName());

    @Override
    public void onCreated(Item item) {
        LOGGER.info("onCreated");
        super.onCreated(item);
    }

    @Override
    public void onLoaded() {
        LOGGER.info("onLoaded");
        super.onLoaded();
    }

    @Override
    public void onDeleted(Item item) {
        LOGGER.info("onDeleted");
        super.onDeleted(item);
    }

    @Override
    public void onRenamed(Item item, String oldName, String newName) {
        LOGGER.info("onRenamed");
        super.onRenamed(item, oldName, newName);

        LOGGER.fine("onChange");
        if( !AbstractProject.class.isAssignableFrom(item.getClass()) ) {
            LOGGER.finer("Is not a Project");
            return;
        }

        // Look for a seed job
        AbstractProject project = (AbstractProject) item;
        GeneratedJobsAction generatedJobsAction = (GeneratedJobsAction) project.getAction(GeneratedJobsAction.class);
        if (generatedJobsAction == null || generatedJobsAction.modifiedJobs == null) {
            LOGGER.finer("Is not a Seed Project, or hasn't been run yet");
            return;
        }

        // 
        Set<String> templates = JenkinsJobManagement.getTemplates(generatedJobsAction.getGeneratedJobs());
        for( String templateName: templates) {
            AbstractProject templateProject = (AbstractProject) Jenkins.getInstance().getItem(templateName);
            SeedJobsProperty seedJobsProp = (SeedJobsProperty) project.getProperty(SeedJobsProperty.class);
            if (seedJobsProp == null || seedJobsProp.seedJobs == null) {
                LOGGER.warning("Is not a Template Project");
                continue;
            }

            boolean removed = seedJobsProp.getSeedJobs().remove(oldName);
            if (!removed) {
                LOGGER.warning("Unable to remove " + oldName + " from template job " + templateProject.getName());
            }
            seedJobsProp.getSeedJobs().add(newName);
        }
    }


}
