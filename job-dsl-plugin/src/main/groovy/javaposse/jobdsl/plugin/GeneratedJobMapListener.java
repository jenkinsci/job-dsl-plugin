package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

@Extension
public class GeneratedJobMapListener extends ItemListener {

    @Override
    public void onDeleted(Item item) {
        GeneratedJobMapHelper.removeSeedReference(item.getFullName());
    }

    @Override
    public void onRenamed(Item item, String oldName, String newName) {
        if (item instanceof AbstractProject) {
            if (GeneratedJobMapHelper.removeSeedReference(item.getFullName().replace(newName, oldName))) {
                GeneratedJobMapHelper.updateTransientActions((AbstractProject) item);
            }
        }
    }
}
