package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.AbstractProject;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;

import java.util.logging.Logger;

/**
 * @author ceilfors
 */
@Extension
public class GeneratedJobMapSaveableListener extends SaveableListener {

    private static final Logger LOGGER = Logger.getLogger(GeneratedJobMapSaveableListener.class.getName());

    @Override
    public void onChange(Saveable saveable, XmlFile file) {
        if (!AbstractProject.class.isAssignableFrom(saveable.getClass())) {
            LOGGER.finest(String.format("%s is not a Project", saveable.getClass()));
            return;
        }

        AbstractProject project = (AbstractProject) saveable;
        if (GeneratedJobMapHelper.removeSeedReference(project.getFullName())) {
            GeneratedJobMapHelper.updateTransientActions(project);
        }
    }
}
